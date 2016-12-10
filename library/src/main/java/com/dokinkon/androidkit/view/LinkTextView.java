package com.dokinkon.androidkit.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * 解決可使用Link HTML的TextView變成Clickable和Focusable的問題
 *
 * @author magiclen
 */
public class LinkTextView extends TextView {

    private boolean linkHit; // 是否為按下連結

    public LinkTextView(Context context) {
        this(context, null);
    }

    public LinkTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinkTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setMovementMethod(LinkTextView.MovementMethod.getInstance());
    }

    // -----物件方法-----
    /**
     * 覆寫onTouchEvent，使其不會永遠傳回true，若為true，則無法將touch事件傳出給上層的View。
     *
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return linkHit;
    }

    /**
     * 設定HTML內容給TextView，如果已啟用AutoLink屬性，則不需要使用這個方法來製作a標籤的連結。
     *
     * @param html
     */
    public void setTextViewHTML(String html) {
        Spanned sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        setText(strBuilder);
    }

    /**
     * 設定MovementMethod之後，TextView會成為Focusable，所以LinkTextView覆寫此方法，永遠傳回false。
     */
    @Override
    public boolean hasFocusable() {
        return false;
    }

    /**
     * 繼承LinkMovementMethod的LinkTextViewMovementMethod，將會針對連結點擊進行處理，
     * 讓LinkTextView知道目前點擊是否為連結點擊。
     *
     * @author magiclen
     *
     */
    public static class MovementMethod extends LinkMovementMethod {

        // -----類別變數-----
        private static MovementMethod sInstance; // 儲存唯一的實體參考

        // -----物件方法-----
        /**
         * 取得LinkTextViewMovementMethod的唯一實體參考。
         *
         * @return
         */
        public static MovementMethod getInstance() {
            if (sInstance == null) {
                sInstance = new MovementMethod(); // 建立新的實體
            }
            return sInstance;
        }

        /**
         * 覆寫觸控事件，分辨出是否為連結的點擊。
         */
        @Override
        public boolean onTouchEvent(@NonNull TextView widget, @NonNull Spannable buffer, @NonNull MotionEvent event) {
            int action = event.getAction(); // 取得事件類型

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

                if (link.length != 0) { // 是連結點擊
                    if (widget instanceof LinkTextView) { // 如果實體是LinkTextView
                        ((LinkTextView) widget).linkHit = true;
                    }
                    if (action == MotionEvent.ACTION_UP) { // 放開時
                        link[0].onClick(widget); // 開啟連結
                    } else if (action == MotionEvent.ACTION_DOWN) { // 按下時
                        Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0])); // 選擇連結
                    }
                    return true;
                } else { // 不是連結點擊
                    if (widget instanceof LinkTextView) { // 如果實體是LinkTextView
                        ((LinkTextView) widget).linkHit = false;
                    }
                    Selection.removeSelection(buffer);
                    Touch.onTouchEvent(widget, buffer, event);
                    return false;
                }
            }
            return Touch.onTouchEvent(widget, buffer, event);
        }
    }
}

