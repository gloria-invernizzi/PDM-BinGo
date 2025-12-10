package com.application.bingo.util.scanner;

import androidx.fragment.app.Fragment;

public abstract class AppCustomScanFragment extends Fragment {
    public abstract String getLastScanned();
    public abstract AppCustomScanFragment setLastScanned(String last);
}
