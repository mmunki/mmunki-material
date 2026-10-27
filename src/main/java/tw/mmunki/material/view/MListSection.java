package tw.mmunki.material.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @deprecated Use {@link tw.mmunki.material.component.ListSection}.
 */
@Deprecated
public class MListSection extends tw.mmunki.material.component.ListSection {

    public MListSection(Context context) {
        super(context);
    }

    public MListSection(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MListSection(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MListCell addCell(CharSequence title) {
        MListCell cell = new MListCell(getContext());
        cell.setTitle(title);
        addCell((tw.mmunki.material.component.ListCell) cell);
        return cell;
    }

    public MListCell addCell(CharSequence title, CharSequence subtitle) {
        MListCell cell = new MListCell(getContext());
        cell.setTitle(title);
        cell.setSubtitle(subtitle);
        addCell((tw.mmunki.material.component.ListCell) cell);
        return cell;
    }
}
