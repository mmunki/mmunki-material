package tw.mmunki.material.view;

import android.content.Context;
import android.view.View;

/**
 * @deprecated Use {@link tw.mmunki.material.component.BottomSheet}.
 */
@Deprecated
public class MBottomSheet extends tw.mmunki.material.component.BottomSheet {

    public MBottomSheet(Context context) {
        super(context);
    }

    public MBottomSheet(Context context, View contentView) {
        super(context, contentView);
    }

    public static MBottomSheet show(Context context, View contentView) {
        MBottomSheet sheet = new MBottomSheet(context, contentView);
        sheet.show();
        return sheet;
    }
}
