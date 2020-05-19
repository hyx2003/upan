package group.j.android.markdownald.util;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import ru.noties.markwon.syntax.Prism4jSyntaxHighlight;
import ru.noties.markwon.syntax.Prism4jTheme;
import ru.noties.markwon.syntax.Prism4jThemeDefault;
import ru.noties.markwon.syntax.SyntaxHighlight;
import ru.noties.prism4j.Prism4j;
import ru.noties.prism4j.annotations.PrismBundle;

/**
 * Implements Markdown syntax highlight partially.
 */
@PrismBundle(
        include = {"markdown"},
        grammarLocatorClassName = ".MyGrammarLocator"
)
public class MarkdownSyntaxHighlighter {
    private static final String TAG = "MarkdownSyntaxHighlight";

    final Prism4j prism4j = new Prism4j(new MyGrammarLocator());
    final Prism4jTheme prism4jTheme = Prism4jThemeDefault.create();
    final SyntaxHighlight highlight = Prism4jSyntaxHighlight.create(prism4j, prism4jTheme);

    public CharSequence highlight(String content) {
        CharSequence charSequence = "";

        if (!content.isEmpty()) {
            charSequence = highlight.highlight("markdown", content);
        }

        return charSequence;
    }

    public void highlight(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            int start;
            int before;
            int count;

            Handler handler = new Handler(Looper.myLooper());
            Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "Before s=" + s + "\tstart=" + start + "\tafter=" + after + "\tcount=" + count);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.start = start;
                this.before = before;
                this.count = count;
                Log.d(TAG, "On s=" + s + "\tstart=" + start + "\tbefore=" + before + "\tcount=" + count);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                handler.removeCallbacks(runnable);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        highlight(s);
                    }
                };
                handler.postDelayed(runnable, 500);
            }

            private void highlight(Editable s) {
                if (s.length() > 0 && (Math.abs(count - before) == 1) && (before == 1 || before == 0)) {
                    Log.d(TAG, "After s=" + s + "\tbefore = " + before + "\tcount = " + count);
                    editText.removeTextChangedListener(this);
                    s.replace(0, s.length(), highlight.highlight("markdown", s.toString()));
                    editText.addTextChangedListener(this);
                }
            }
        });
    }

}
