package com.ancientlore.stickies

import android.support.annotation.StringDef

@StringDef(C.FIELD_TITLE, C.FIELD_DATE)
@Retention(AnnotationRetention.SOURCE)
annotation class SortField