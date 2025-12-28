package com.application.bingo.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bingo.R;
import com.application.bingo.ui.adapter.FamilyMemberAdapter;
import com.application.bingo.ui.viewmodel.FamilyViewModel;
import com.application.bingo.ui.viewmodel.ProfileViewModel;
import com.application.bingo.ui.viewmodel.ViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class FamilyFragment extends Fragment {

    private FamilyViewModel familyViewModel;
    private ProfileViewModel profileViewModel;

    private LinearLayout layoutNoFamily;
    private LinearLayout layoutHasFamily;
    private TextView textFamilyCode;
    private RecyclerView recyclerMembers;
    private FamilyMemberAdapter adapter;
    private TextInputEditText inputFamilyCode;

    private String currentUserEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_family, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewModelFactory factory = new ViewModelFactory(requireActivity().getApplication());
        familyViewModel = new ViewModelProvider(this, factory).get(FamilyViewModel.class);
        profileViewModel = new ViewModelProvider(this, factory).get(ProfileViewModel.class);

        layoutNoFamily = view.findViewById(R.id.layout_no_family);
        layoutHasFamily = view.findViewById(R.id.layout_has_family);
        textFamilyCode = view.findViewById(R.id.text_family_code);
        recyclerMembers = view.findViewById(R.id.recycler_family_members);
        inputFamilyCode = view.findViewById(R.id.input_family_code);
        Button btnCreate = view.findViewById(R.id.btn_create_family);
        Button btnJoin = view.findViewById(R.id.btn_join_family);
        Button btnLeave = view.findViewById(R.id.btn_leave_family);

        // Setup RecyclerView
        adapter = new FamilyMemberAdapter(new ArrayList<>());
        recyclerMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerMembers.setAdapter(adapter);

        // Get current email from FirebaseAuth
        if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserEmail = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getEmail();
            if (currentUserEmail != null) {
                familyViewModel.checkUserFamily(currentUserEmail);
            }
        }

        // Observe ViewModel
        familyViewModel.getFamilyId().observe(getViewLifecycleOwner(), familyId -> {
            if (familyId != null && !familyId.isEmpty()) {
                showFamilyView(familyId);
            } else {
                showNoFamilyView();
            }
        });

        familyViewModel.getFamilyMembers().observe(getViewLifecycleOwner(), members -> {
            if (members != null) {
                adapter.setMembers(members);
            }
        });

        familyViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
        
        familyViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });

        // Listeners
        btnCreate.setOnClickListener(v -> {
            if (currentUserEmail != null) {
                familyViewModel.createFamily(currentUserEmail);
            }
        });

        btnJoin.setOnClickListener(v -> {
            String code = inputFamilyCode.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                inputFamilyCode.setError("Inserisci un codice");
                return;
            }
            if (currentUserEmail != null) {
                familyViewModel.joinFamily(currentUserEmail, code);
            }
        });

        btnLeave.setOnClickListener(v -> {
            if (currentUserEmail != null) {
                familyViewModel.leaveFamily(currentUserEmail);
            }
        });
    }

    private void showFamilyView(String familyId) {
        layoutNoFamily.setVisibility(View.GONE);
        layoutHasFamily.setVisibility(View.VISIBLE);
        textFamilyCode.setText(familyId);
    }

    private void showNoFamilyView() {
        layoutHasFamily.setVisibility(View.GONE);
        layoutNoFamily.setVisibility(View.VISIBLE);
    }
}
