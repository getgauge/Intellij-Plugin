package com.thoughtworks.gauge.markdownPreview;

import com.vladsch.flexmark.ast.HtmlInnerBlock;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.util.NodeTracker;
import com.vladsch.flexmark.util.collection.iteration.ReversiblePeekingIterator;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.CharSubSequence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagPostProcessor extends NodePostProcessor {
    public TagPostProcessor() {

    }

    @Override
    public void process(NodeTracker state, Node node) {
        if (node instanceof Paragraph) {
            ReversiblePeekingIterator<Node> iterator = node.getChildIterator();
            while (iterator.hasNext()) {
                Node node1 = iterator.next();

                BasedSequence nodeText = node1.getChars();
                Pattern regex = Pattern.compile("^\\s*[Tt][Aa][Gg][Ss]\\s*:.*$");
                Matcher regexMatcher = regex.matcher(nodeText);
                if (node1 instanceof Text && regexMatcher.matches()) {
                    HtmlInnerBlock block = new HtmlInnerBlock(CharSubSequence.of("<span class=\"tag\">" + nodeText + "</span>"));
                    node1.insertBefore(block);
                    state.nodeAddedWithChildren(block);
                    node1.unlink();
                    state.nodeRemoved(node1);
                }
            }
        }
    }
}
