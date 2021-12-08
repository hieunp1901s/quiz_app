package com.example.quiz.ui.fragment;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quiz.adapter.MyTestsAdapter;

import com.example.quiz.models.Question;
import com.example.quiz.models.TeacherTabFragmentItemClicked;
import com.example.quiz.models.Test;
import com.example.quiz.databinding.FragmentTeacherTabBinding;
import com.example.quiz.ui.dialog.AddTestDialogFragment;
import com.example.quiz.ui.dialog.MyTestInfoDialogFragment;
import com.example.quiz.viewmodels.FirebaseViewModel;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;

import java.util.Iterator;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TeacherTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherTabFragment extends Fragment  implements PickiTCallbacks, TeacherTabFragmentItemClicked {
    PickiT pickiT;
    DataFormatter formatter = new DataFormatter(Locale.US);
    FragmentTeacherTabBinding binding;
    FirebaseViewModel firebaseViewModel;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        pickiT.getPath(uri, Build.VERSION.SDK_INT);
                        firebaseViewModel.getProgressing().setValue(true);
                    }
                }
            });
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TeacherTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeacherTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TeacherTabFragment newInstance(String param1, String param2) {
        TeacherTabFragment fragment = new TeacherTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        pickiT = new PickiT(getContext(), this, getActivity());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTeacherTabBinding.inflate(inflater, container, false);

        binding.button.setOnClickListener(v -> mGetContent.launch("application/excel/*"));
        firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);

        RecyclerView.LayoutManager layoutManager =new LinearLayoutManager(getContext());
        binding.rvMyTests.setLayoutManager(layoutManager);
        MyTestsAdapter myTestsAdapter = new MyTestsAdapter(firebaseViewModel.getListMyTest(), this);
        binding.rvMyTests.setAdapter(myTestsAdapter);

        firebaseViewModel.getNotifyMyTestDataChanged().observe(getViewLifecycleOwner(), notify -> myTestsAdapter.notifyAdapter());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public Test readExcel(String path) {
        Test test = new Test();
        try {
            FileInputStream excelFile = new FileInputStream(new File(path));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);

            for (Row currentRow : datatypeSheet) {
                Iterator<Cell> cellIterator = currentRow.iterator();
                String[] temp = {"", "", "", "", "", ""};
                int index = 0;
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    temp[index] = formatter.formatCellValue(currentCell);
                    index++;
                }
                test.addQuestion(new Question(temp[0], temp[1], temp[2], temp[3], temp[4], temp[5]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return test;
    }

    public String fileNameFromPath(String path) {
        return path.substring(path.lastIndexOf("/")+1);
    }


    @Override
    public void PickiTonUriReturned() {
    }

    @Override
    public void PickiTonStartListener() {
    }

    @Override
    public void PickiTonProgressUpdate(int progress) {

    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {
        Test test = readExcel(path);
        Log.d("test size", test.getListQuestion().size() + "");
        DialogFragment dialog = new AddTestDialogFragment(fileNameFromPath(path), test);
        dialog.setCancelable(false);
        dialog.show(getParentFragmentManager(), "add test dialog");
        firebaseViewModel.getProgressing().setValue(false);
    }

    @Override
    public void onItemClicked(Test test) {
        DialogFragment dialog = new MyTestInfoDialogFragment(test);
        dialog.show(getParentFragmentManager(), "my test info");
    }
}