package com.thoughtworks.gauge.core;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

public class GaugeConnection {

    private final int port;
    private Socket gaugeSocket;

    public GaugeConnection(int port) {
        this.port = port;
        createConnection(5);
    }

    private void createConnection(int tries) {
        if (tries == 0) {
            throw new RuntimeException("Gauge API not started");
        }
        try {
            gaugeSocket = new Socket("localhost", port);
        } catch (IOException e) {
            try {
                //waits for the process to start accepting connection
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            createConnection(tries - 1);
        }
    }

    public List<String> fetchAllSteps() throws IOException {
        //todo: expose some helpers on gauge-java for sending and receiving proto messages
        Api.APIMessage message = getStepRequest();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CodedOutputStream cos = CodedOutputStream.newInstance(stream);
        byte[] bytes = message.toByteArray();
        cos.writeRawVarint64(bytes.length);
        cos.flush();
        stream.write(bytes);
        gaugeSocket.getOutputStream().write(stream.toByteArray());
        gaugeSocket.getOutputStream().flush();

        InputStream remoteStream = gaugeSocket.getInputStream();
        MessageLength messageLength = getMessageLength(remoteStream);
        bytes = toBytes(messageLength);
        Api.APIMessage apiMessage = Api.APIMessage.parseFrom(bytes);
        Api.GetAllStepsResponse allStepsResponse = apiMessage.getAllStepsResponse();
        return allStepsResponse.getStepsList();
    }

    private Api.APIMessage getStepRequest() {
        Api.GetAllStepsRequest stepRequest = Api.GetAllStepsRequest.newBuilder().build();
        return Api.APIMessage.newBuilder().setMessageType(Api.APIMessage.APIMessageType.GetAllStepsRequest).setMessageId(2).setAllStepsRequest(stepRequest).build();
    }

    static class MessageLength {
        public long length;
        public CodedInputStream remainingStream;

        public MessageLength(long length, CodedInputStream remainingStream) {
            this.length = length;
            this.remainingStream = remainingStream;
        }
    }

    private static MessageLength getMessageLength(InputStream is) throws IOException {
        CodedInputStream codedInputStream = CodedInputStream.newInstance(is);
        long size = codedInputStream.readRawVarint64();
        return new MessageLength(size, codedInputStream);
    }

    private static byte[] toBytes(MessageLength messageLength) throws IOException {
        long messageSize = messageLength.length;
        CodedInputStream stream = messageLength.remainingStream;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 0; i < messageSize; i++) {
            outputStream.write(stream.readRawByte());
        }

        return outputStream.toByteArray();
    }
}
