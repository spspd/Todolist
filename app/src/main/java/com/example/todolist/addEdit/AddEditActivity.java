package com.example.todolist.addEdit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.todolist.R;
import com.example.todolist.room.MyDatabase;
import com.example.todolist.room.Todoitem;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class AddEditActivity extends AppCompatActivity {
    private TextInputLayout til_title, til_sDate, til_dDate, til_memo;
    private ImageButton ib_sDate, id_dDate;
    private final int START_DATE = 0;
    private final int DUE_DATE = 1;
    private int mode;
    private int id;
    private Todoitem selectItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);


        Intent main_Intent = getIntent();
        mode = main_Intent.getIntExtra("mode", 0);


        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); //뒤로가기 버튼 띄운다
            if (mode == 0) {
                actionBar.setTitle("Add Todo");
            } else {
                actionBar.setTitle("Edit Todo");
            }
        }


        til_title = findViewById(R.id.addEdit_til_title);
        til_sDate = findViewById(R.id.addEdit_til_start);
        til_dDate = findViewById(R.id.addEdit_til_due);
        til_memo = findViewById(R.id.addEdit_til_memo);

        ib_sDate = findViewById(R.id.addEdit_ibtn_start);
        id_dDate = findViewById(R.id.addEdit_ibtn_due);

        if(mode == 1) {
            id = getIntent().getIntExtra("item_id", -1);
            if(id == -1){
                Log.e("todo_id", "item id wrong");
                Toast.makeText(AddEditActivity.this, "item id wrong", Toast.LENGTH_SHORT).show();
            }else{
                MyDatabase myDatabase = MyDatabase.getInstance(AddEditActivity.this);
                try{
                    selectItem =myDatabase.todoDao().getTodo(id); // 4주차 였나
                }catch(Exception e){
                    e.printStackTrace();
                }
                til_title.getEditText().setText(selectItem.getTitle());
                til_title.getEditText().setText(selectItem.getStart());
                til_title.getEditText().setText(selectItem.getDue());
                til_title.getEditText().setText(selectItem.getMemo());
                }
        }

        ib_sDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalender(START_DATE);
            }
        });

        id_dDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalender(DUE_DATE);
            }
        });

    }
    public void showCalender(final int mode){
        // 캘린더 띄우는 동작을 실행
        Calendar cal = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDate = cal.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(AddEditActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                String s_month = new Integer(month+1).toString();
                String s_date = new Integer(dayOfMonth).toString();

                if(month+1 < 10 )
                    s_month = "0" + new Integer(month+1).toString();
                if(dayOfMonth < 10)
                    s_date = "0" + new Integer(dayOfMonth).toString();

                String date = year + "/" + s_month + "/" + s_date;
                if(mode == START_DATE){
                    til_sDate.getEditText().setText(date);
                }else if(mode == DUE_DATE){
                    til_dDate.getEditText().setText(date);
                }

            }
        }, mYear, mMonth, mDate).show();
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.home:
                finish();
                break;
            case R.id.save_todo:
                String title = til_title.getEditText().getText().toString();
                String sDate = til_sDate.getEditText().getText().toString();
                String dDate = til_dDate.getEditText().getText().toString();
                String memo = til_memo.getEditText().getText().toString();

                if(title.equals(""))
                    til_title.setError("필수요소입니다!");
                else
                    til_title.setError(null);
                if(sDate.equals(""))
                    til_sDate.setError("필수요소입니다!");
                else
                    til_dDate.setError(null);                if(title.equals(""))
                til_dDate.setError("필수요소입니다!");
            else
                til_dDate.setError(null);

            if(!title.equals("") && !sDate.equals("") && !dDate.equals("")) {
                if(sDate.compareTo(dDate)>0) {
                    til_sDate.setError("시작 날짜가 더 느려요");
                    til_dDate.setError("끝나는 날짜가 더 느려요");
                }else{
                    MyDatabase myDatabase = MyDatabase.getInstance(AddEditActivity.this);

                    if(mode == 0) {
                        //추가
                        Todoitem todoitem = new Todoitem(title, sDate, dDate, memo);
                        myDatabase.todoDao().insertTodo(todoitem);
                    }else if(mode == 1){
                        //수정
                        selectItem.setTitle(title);
                        selectItem.setStart(sDate);
                        selectItem.setDue(dDate);
                        selectItem.setMemo(memo);

                        myDatabase.todoDao().editTodo(selectItem);
                        Toast.makeText(AddEditActivity.this, "저장성공", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

        }

        return super.onOptionsItemSelected(item);
    }
}