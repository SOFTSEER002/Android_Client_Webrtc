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
        sViewsWithIds.put(R.id.remote_video_layout, 2);
        sViewsWithIds.put(R.id.remote_video_view, 3);
        sViewsWithIds.put(R.id.local_video_layout, 4);
        sViewsWithIds.put(R.id.local_video_view, 5);
        sViewsWithIds.put(R.id.call_fragment_container, 6);
        sViewsWithIds.put(R.id.contact_name_call, 7);
        sViewsWithIds.put(R.id.buttons_call_container, 8);
        sViewsWithIds.put(R.id.button_call_disconnect, 9);
        sViewsWithIds.put(R.id.button_call_switch_camera, 10);
        sViewsWithIds.put(R.id.button_call_toggle_mic, 11);
        sViewsWithIds.put(R.id.capture_format_text_call, 12);
        sViewsWithIds.put(R.id.text, 13);
        sViewsWithIds.put(R.id.capture_format_slider_call, 14);
        sViewsWithIds.put(R.id.stats, 15);
        sViewsWithIds.put(R.id.batTemp, 16);
        sViewsWithIds.put(R.id.batTempsocket, 17);
        sViewsWithIds.put(R.id.batlvl, 18);
        sViewsWithIds.put(R.id.batlvlsocket, 19);
        sViewsWithIds.put(R.id.networksignal, 20);
        sViewsWithIds.put(R.id.networksignalsocket, 21);
        sViewsWithIds.put(R.id.wifisignal, 22);
        sViewsWithIds.put(R.id.wifisignalsocket, 23);
        sViewsWithIds.put(R.id.feedback_tv, 24);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityCallBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 25, sIncludes, sViewsWithIds));
    }
    private ActivityCallBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.ImageView) bindings[16]
            , (android.widget.TextView) bindings[17]
            , (android.widget.ImageView) bindings[18]
            , (android.widget.TextView) bindings[19]
            , (android.widget.ImageButton) bindings[9]
            , (android.widget.ImageButton) bindings[10]
            , (android.widget.ImageButton) bindings[11]
            , (android.widget.LinearLayout) bindings[8]
            , (android.widget.FrameLayout) bindings[6]
            , (android.widget.SeekBar) bindings[14]
            , (android.widget.TextView) bindings[12]
            , (android.widget.TextView) bindings[7]
            , (android.widget.Button) bindings[24]
            , (android.widget.RelativeLayout) bindings[1]
            , (com.myhexaville.androidwebrtc.app_rtc_sample.view.PercentFrameLayout) bindings[4]
            , (org.webrtc.SurfaceViewRenderer) bindings[5]
            , (android.widget.ImageView) bindings[20]
            , (android.widget.TextView) bindings[21]
            , (com.myhexaville.androidwebrtc.app_rtc_sample.view.PercentFrameLayout) bindings[2]
            , (org.webrtc.SurfaceViewRenderer) bindings[3]
            , (android.widget.TextView) bindings[15]
            , (android.widget.TextView) bindings[13]
            , (android.widget.ImageView) bindings[22]
            , (android.widget.TextView) bindings[23]
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