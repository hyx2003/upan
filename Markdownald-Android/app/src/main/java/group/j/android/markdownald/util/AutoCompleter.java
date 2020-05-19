package group.j.android.markdownald.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Complete the markdown syntax automatically
 */

public class AutoCompleter {

    private EditText et;

    public AutoCompleter(EditText et){
        this.et = et;
    }

    public void run(){
        addMyTextListener();
    }

    private void addMyTextListener(){
        et.addTextChangedListener(new TextWatcher() {
            char add;
            int point = 0;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==1) {
                    add = s.charAt(start);
                    point = start+1;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(add == '*'){
                    et.removeTextChangedListener(this);
                    s.insert(point, "*");
                    addMyTextListener();
                    et.setSelection(point);
                }
            }
        });
    }

}
