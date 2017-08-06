package com.reforms.cf.struct;

/**
 * Help for StackMapFrame api
 * @author evgenie
 */
public class StackMapFrames {

    public static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED_TAG = 247;
    public static final int SAME_FRAME_EXTENDED_TAG = 251;
    public static final int FULL_FRAME_TAG = 255;

    public static final String SAME_FRAME = "same_frame";
    public static final String SAME_LOCALS_1_STACK_ITEM_FRAME = "same_locals_1_stack_item_frame";
    public static final String SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = "same_locals_1_stack_item_frame_extended";
    public static final String CHOP_FRAME = "chop_frame";
    public static final String SAME_FRAME_EXTENDED = "same_frame_extended";
    public static final String APPEND_FRAME = "append_frame";
    public static final String FULL_FRAME = "full_frame";

    public static boolean isSameFrame(int tag) {
        return tag >= 0 && tag <= 63;
    }

    public static boolean isSameLocalsStackItemFrame(int tag) {
        return tag >= 64 && tag <= 127;
    }

    public static boolean isSameLocalsStackItemFrameExtended(int tag) {
        return tag == SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED_TAG;
    }

    public static boolean isChopFrame(int tag) {
        return tag >= 248 && tag <= 250;
    }

    public static boolean isSameFrameExtended(int tag) {
        return tag == SAME_FRAME_EXTENDED_TAG;
    }

    public static boolean isAppendFrame(int tag) {
        return tag >= 252 && tag <= 254;
    }

    public static boolean isFullFrame(int tag) {
        return tag == FULL_FRAME_TAG;
    }
}
