package com.randomappsinc.randomnumbergeneratorplus.constants;

import androidx.annotation.IntDef;

@IntDef({
        RNGType.NUMBER,
        RNGType.DICE,
        RNGType.LOTTO,
        RNGType.COINS,
})
public @interface RNGType {
    int NUMBER = 0;
    int DICE = 1;
    int LOTTO = 2;
    int COINS = 3;
}
