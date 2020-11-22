package com.fijimf.deepfijomega.utils;

import com.fijimf.deepfijomega.entity.content.Post;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class MarkdownRenderer {
    private  final Parser parser= Parser.builder(getMarkupRendererOptions()).build();;
    private  final HtmlRenderer renderer= HtmlRenderer.builder(getMarkupRendererOptions()).build();
    public  final Logger logger = LoggerFactory.getLogger(Post.class);

    @NotNull
    private  MutableDataSet getMarkupRendererOptions() {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));
        return options;
    }

    public String renderMarkup(String markup) {
        // You can re-use parser and renderer instances
        String html = renderer.render(parser.parse(markup));  // "<p>This is <em>Sparta</em></p>\n"
        logger.info(html);
        return html;
    }
}

