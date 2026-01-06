package com.example.doan_mau;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgressActivity extends AppCompatActivity {
    private RecyclerView rvTodo, rvTimetable;
    private TodoAdapter todoAdapter, timetableAdapter;
    private List<String> todoList, timetableList;
    private SharedPreferences sp;
    private Spinner spnDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // 1. Ánh xạ các View
        rvTodo = findViewById(R.id.rvTodo);
        rvTimetable = findViewById(R.id.rvTimetable);
        spnDay = findViewById(R.id.spnDayOfWeek);
        sp = getSharedPreferences("StudentTasks", MODE_PRIVATE);

        // 2. Thiết lập Spinner chọn Thứ (Rất quan trọng để setup)
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(this,
                R.array.days_of_week, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDay.setAdapter(dayAdapter);

        // 3. Load dữ liệu từ bộ nhớ
        loadTasks();

        // 4. Thiết lập Adapter cho Việc cần làm
        todoAdapter = new TodoAdapter(todoList, position -> showEditMenu(position, true));
        rvTodo.setLayoutManager(new LinearLayoutManager(this));
        rvTodo.setAdapter(todoAdapter);

        // 5. Thiết lập Adapter cho Lịch học
        timetableAdapter = new TodoAdapter(timetableList, position -> showEditMenu(position, false));
        rvTimetable.setLayoutManager(new LinearLayoutManager(this));
        rvTimetable.setAdapter(timetableAdapter);

        // 6. Sự kiện nút bấm
        findViewById(R.id.btnAddTodo).setOnClickListener(v -> showInputDialog("Việc cần làm", false, -1));
        findViewById(R.id.btnAddTimetable).setOnClickListener(v -> showInputDialog("Lịch học", true, -1));
    }

    // Menu Sửa/Xóa khi bấm vào item
    private void showEditMenu(int position, boolean isTodo) {
        String[] options = {"Cập nhật (Update)", "Xóa bỏ"};
        new AlertDialog.Builder(this).setTitle("Quản lý mục này").setItems(options, (d, which) -> {
            if (which == 0) showInputDialog("Cập nhật", !isTodo, position);
            else deleteItem(position, isTodo);
        }).show();
    }

    private void deleteItem(int position, boolean isTodo) {
        if (isTodo) todoList.remove(position);
        else timetableList.remove(position);
        saveToSP();
        updateUI();
        Toast.makeText(this, "Đã xóa!", Toast.LENGTH_SHORT).show();
    }

    private void showInputDialog(String type, boolean isTimetable, int position) {
        EditText input = new EditText(this);
        // Nếu là sửa, hiện lại tên cũ
        if (position != -1) {
            String fullData = isTimetable ? timetableList.get(position) : todoList.get(position);
            String currentName = fullData.split("\\|")[0].replaceAll("\\[.*?\\] ", "").replace("Lịch học: ", "");
            input.setText(currentName);
        }

        new AlertDialog.Builder(this)
                .setTitle(position == -1 ? "Thêm " + type : "Sửa " + type)
                .setMessage("Nhập tên và đảm bảo bạn đã chọn Thứ ở thanh chọn phía ngoài.")
                .setView(input)
                .setPositiveButton("Tiếp theo (Chọn giờ)", (d, w) -> {
                    String name = input.getText().toString();
                    if (!name.isEmpty()) pickTime(name, isTimetable, position);
                }).show();
    }

    private void pickTime(String name, boolean isTimetable, int position) {
        Calendar now = Calendar.getInstance();
        new TimePickerDialog(this, (v, h, m) -> {
            String timeStr = String.format("%02d:%02d", h, m);
            String day = spnDay.getSelectedItem().toString(); // Lấy thứ từ Spinner
            String finalData = "[" + day + "] " + (isTimetable ? "Lịch học: " : "") + name + "|" + timeStr;

            if (isTimetable) {
                if (position != -1) timetableList.set(position, finalData);
                else timetableList.add(finalData);
            } else {
                if (position != -1) todoList.set(position, finalData);
                else todoList.add(finalData);
            }

            saveToSP(); // Lưu vào máy
            updateUI(); // Cập nhật màn hình ngay lập tức
            setupAlarmRealTime(name, h, m, isTimetable); // Đặt báo thức
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();
    }

    private void setupAlarmRealTime(String name, int h, int m, boolean isTimetable) {
        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, h);
        target.set(Calendar.MINUTE, m);
        target.set(Calendar.SECOND, 0);

        long triggerTime = target.getTimeInMillis();
        String msg = isTimetable ? "Bạn ơi! 30p nữa đến lịch học: " + name : "Bạn ơi! Đến giờ làm: " + name;
        if (isTimetable) triggerTime -= (30 * 60 * 1000);

        if (triggerTime <= System.currentTimeMillis()) triggerTime += (24 * 60 * 60 * 1000);

        setAlarm(triggerTime, msg);
    }

    private void setAlarm(long time, String msg) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && am != null && !am.canScheduleExactAlarms()) {
            startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
            return;
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("message", msg);
        PendingIntent pi = PendingIntent.getBroadcast(this, (int)System.currentTimeMillis(),
                intent, PendingIntent.FLAG_IMMUTABLE);

        if (am != null) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pi);
    }

    private void loadTasks() {
        // Sử dụng 2 key khác nhau để không bị đè dữ liệu
        Set<String> setTodo = sp.getStringSet("todos_data", new HashSet<>());
        Set<String> setTime = sp.getStringSet("timetables_data", new HashSet<>());
        todoList = new ArrayList<>(setTodo);
        timetableList = new ArrayList<>(setTime);
    }

    private void saveToSP() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet("todos_data", new HashSet<>(todoList));
        editor.putStringSet("timetables_data", new HashSet<>(timetableList));
        editor.apply();
    }

    private void updateUI() {
        // Ép các RecyclerView vẽ lại dữ liệu mới
        todoAdapter.notifyDataSetChanged();
        timetableAdapter.notifyDataSetChanged();
    }
}