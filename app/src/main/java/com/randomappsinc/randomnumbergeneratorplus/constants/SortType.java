package com.randomappsinc.randomnumbergeneratorplus.constants;

import androidx.annotation.IntDef;

@IntDef({
        SortType.NONE,
        SortType.ASCENDING,
        SortType.DESCENDING,
})
public @interface SortType {
    int NONE = 0;
    int ASCENDING = 1;
    int DESCENDING = 2;
}
