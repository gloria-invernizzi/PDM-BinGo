package com.application.bingo.util.scanner;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.application.bingo.ui.viewmodel.ProductViewModel;

public abstract class AppCustomScanFragment extends Fragment {
    public abstract String getLastScanned();
    public abstract AppCustomScanFragment setLastScanned(String last);
    public abstract ProductViewModel getProductViewModel();
}
