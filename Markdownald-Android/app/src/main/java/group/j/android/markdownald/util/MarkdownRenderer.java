package group.j.android.markdownald.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.widget.TextView;

import org.commonmark.node.Node;
import org.commonmark.node.SoftLineBreak;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.core.CorePlugin;
import ru.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import ru.noties.markwon.ext.tables.TablePlugin;

/**
 * Implements markdown rendering.
 */
public class MarkdownRenderer {
    private final Markwon markwon;

    public MarkdownRenderer(Context context) {
        this.markwon = Markwon.builder(context)
                .usePlugin(CorePlugin.create())
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(TablePlugin.create(context))
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
                        builder.on(SoftLineBreak.class, new MarkwonVisitor.NodeVisitor<SoftLineBreak>() {
                            @Override
                            public void visit(@NonNull MarkwonVisitor visitor, @NonNull SoftLineBreak softLineBreak) {
                                visitor.forceNewLine();
                            }
                        });
                    }
                })
                .build();
    }

    public void setMarkdown(TextView textView, String content) {
        Node node = markwon.parse(content);
        Spanned markdown = markwon.render(node);
        if (markdown.length() != 0) {
            markwon.setParsedMarkdown(textView, markdown);
        } else {
            textView.setText(content);
        }
    }

}
