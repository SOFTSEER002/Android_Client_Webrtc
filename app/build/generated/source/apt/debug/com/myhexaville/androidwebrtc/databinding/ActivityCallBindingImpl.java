package com.myhexaville.androidwebrtc.databinding;
import com.myhexaville.androidwebrtc.R;
import com.myhexaville.androidwebrtc.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityCallBindingImpl extends ActivityCallBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.header, 1);
        sViewsWithIds.put(R.id.tempdata, 2);
        sViewsWithIds.put(R.id.remote_video_layout, 3);
        sViewsWithIds.put(R.id.remote_video_view, 4);
        sViewsWithIds.put(R.id.local_video_layout, 5);
        sViewsWithIds.put(R.id.local_video_view, 6);
        sViewsWithIds.put(R.id.call_fragment_container, 7);
        sViewsWithIds.put(R.id.contact_name_call, 8);
        sViewsWithIds.put(R.id.buttons_call_container, 9);
        sViewsWithIds.put(R.id.button_call_disconnect, 10);
        sViewsWithIds.put(R.id.button_call_switch_camera, 11);
        sViewsWithIds.put(R.id.button_call_toggle_mic, 12);
        sViewsWithIds.put(R.id.capture_format_text_call, 13);
        sViewsWithIds.put(R.id.text, 14);
        sViewsWithIds.put(R.id.capture_format_slider_call, 15);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityCallBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 16, sIncludes, sViewsWithIds));
    }
    private ActivityCallBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.ImageButton) bindings[10]
            , (android.widget.ImageButton) bindings[11]
            , (android.widget.ImageButton) bindings[12]
            , (android.widget.LinearLayout) bindings[9]
            , (android.widget.FrameLayout) bindings[7]
            , (android.widget.SeekBar) bindings[15]
            , (android.widget.TextView) bindings[13]
            , (android.widget.TextView) bindings[8]
            , (android.widget.LinearLayout) bindings[1]
            , (com.myhexaville.androidwebrtc.app_rtc_sample.view.PercentFrameLayout) bindings[5]
            , (org.webrtc.SurfaceViewRenderer) bindings[6]
            , (com.myhexaville.androidwebrtc.app_rtc_sample.view.PercentFrameLayout) bindings[3]
            , (org.webrtc.SurfaceViewRenderer) bindings[4]
            , (android.widget.TextView) bindings[2]
            , (android.widget.TextView) bindings[14]
            );
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}