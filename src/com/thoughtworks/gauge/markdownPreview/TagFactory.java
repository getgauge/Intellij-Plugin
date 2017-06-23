package com.thoughtworks.gauge.markdownPreview;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;

public class TagFactory extends NodePostProcessorFactory {
    public TagFactory(boolean ignored) {
        super(ignored);
        addNodeWithExclusions(Paragraph.class,
                ListBlock.class, Link.class, LinkNode.class, CodeBlock.class, Heading.class, Image.class, ListItem.class,
                Code.class, HtmlBlockBase.class, TableBlock.class);
    }

    @Override
    public NodePostProcessor create(Document document) {
        return new TagPostProcessor();
    }
}
