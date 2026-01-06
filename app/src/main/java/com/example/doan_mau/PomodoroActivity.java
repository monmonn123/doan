package com.example.doan_mau;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PomodoroActivity extends AppCompatActivity {

    private enum Mode {
        POMODORO, SHORT_BREAK, LONG_BREAK
    }

    private Mode currentMode = Mode.POMODORO;
    private long timePomodoro = 25 * 60 * 1000;
    private long timeShortBreak = 5 * 60 * 1000;
    private long timeLongBreak = 15 * 60 * 1000;

    private long timeLeftInMillis;
    private CountDownTimer countDownTimer;
    private boolean timerRunning;

    private LinearLayout mainLayout;
    private TextView tvTimer, tvCurrentTask;
    private TextView tabPomodoro, tabShortBreak, tabLongBreak;
    private Button btnStartStop;

    private RecyclerView rvTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        mainLayout = findViewById(R.id.mainLayout);
        tvTimer = findViewById(R.id.tvTimer);
        tvCurrentTask = findViewById(R.id.tvCurrentTask);
        tabPomodoro = findViewById(R.id.tabPomodoro);
        tabShortBreak = findViewById(R.id.tabShortBreak);
        tabLongBreak = findViewById(R.id.tabLongBreak);
        btnStartStop = findViewById(R.id.btnStartStop);
        rvTasks = findViewById(R.id.rvTasks);

        timeLeftInMillis = timePomodoro;
        updateCountDownText();

        tabPomodoro.setOnClickListener(v -> switchMode(Mode.POMODORO));
        tabShortBreak.setOnClickListener(v -> switchMode(Mode.SHORT_BREAK));
        tabLongBreak.setOnClickListener(v -> switchMode(Mode.LONG_BREAK));

        btnStartStop.setOnClickListener(v -> {
            if (timerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        findViewById(R.id.btnSettings).setOnClickListener(v -> showSettingsDialog());
        findViewById(R.id.btnAdd).setOnClickListener(v -> showAddTaskDialog());

        // Setup RecyclerView
        taskList = new ArrayList<>();
        taskList.add(new Task("Đang học Lập trình Web", false));
        taskAdapter = new TaskAdapter(taskList);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);

        updateCurrentTaskLabel();
    }

    private void switchMode(Mode mode) {
        if (timerRunning) pauseTimer();
        currentMode = mode;

        tabPomodoro.setBackgroundColor(Color.TRANSPARENT);
        tabShortBreak.setBackgroundColor(Color.TRANSPARENT);
        tabLongBreak.setBackgroundColor(Color.TRANSPARENT);

        int bgColor;
        switch (mode) {
            case SHORT_BREAK:
                bgColor = Color.parseColor("#38858A");
                timeLeftInMillis = timeShortBreak;
                tabShortBreak.setBackgroundColor(Color.parseColor("#33FFFFFF"));
                break;
            case LONG_BREAK:
                bgColor = Color.parseColor("#397097");
                timeLeftInMillis = timeLongBreak;
                tabLongBreak.setBackgroundColor(Color.parseColor("#33FFFFFF"));
                break;
            default:
                bgColor = Color.parseColor("#BA4949");
                timeLeftInMillis = timePomodoro;
                tabPomodoro.setBackgroundColor(Color.parseColor("#33FFFFFF"));
                break;
        }

        mainLayout.setBackgroundColor(bgColor);
        btnStartStop.setTextColor(bgColor);
        updateCountDownText();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                btnStartStop.setText("START");
                Toast.makeText(PomodoroActivity.this, "Hết giờ!", Toast.LENGTH_SHORT).show();
            }
        }.start();

        timerRunning = true;
        btnStartStop.setText("STOP");
    }

    private void pauseTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        timerRunning = false;
        btnStartStop.setText("START");
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cài đặt thời gian (phút)");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText inputPomo = createSettingInput("Pomodoro", timePomodoro / 60000);
        final EditText inputShort = createSettingInput("Short Break", timeShortBreak / 60000);
        final EditText inputLong = createSettingInput("Long Break", timeLongBreak / 60000);

        layout.addView(inputPomo);
        layout.addView(inputShort);
        layout.addView(inputLong);

        builder.setView(layout);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            try {
                timePomodoro = Long.parseLong(inputPomo.getText().toString()) * 60000;
                timeShortBreak = Long.parseLong(inputShort.getText().toString()) * 60000;
                timeLongBreak = Long.parseLong(inputLong.getText().toString()) * 60000;
                switchMode(currentMode);
            } catch (Exception e) {
                Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private EditText createSettingInput(String hint, long currentVal) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setText(String.valueOf(currentVal));
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        return input;
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm công việc mới");

        final EditText input = new EditText(this);
        input.setHint("Bạn định làm gì?");
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                taskList.add(new Task(name, false));
                taskAdapter.notifyItemInserted(taskList.size() - 1);
                updateCurrentTaskLabel();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void updateCurrentTaskLabel() {
        if (taskList.isEmpty()) {
            tvCurrentTask.setText("Hãy thêm một công việc!");
        } else {
            // Hiển thị task đầu tiên chưa hoàn thành
            for (Task t : taskList) {
                if (!t.isDone) {
                    tvCurrentTask.setText(t.name);
                    return;
                }
            }
            tvCurrentTask.setText("Tất cả đã xong!");
        }
    }

    // Task Model
    private static class Task {
        String name;
        boolean isDone;

        Task(String name, boolean isDone) {
            this.name = name;
            this.isDone = isDone;
        }
    }

    // Adapter for Task RecyclerView
    private class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
        private List<Task> tasks;

        TaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_pomodoro, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Task task = tasks.get(position);
            holder.tvTaskName.setText(task.name);
            holder.cbDone.setChecked(task.isDone);

            applyTaskStyle(holder, task.isDone);

            holder.cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.isDone = isChecked;
                applyTaskStyle(holder, isChecked);
                updateCurrentTaskLabel();
            });

            holder.btnEditTask.setOnClickListener(v -> showEditTaskDialog(task, position));
            holder.itemView.setOnClickListener(v -> showEditTaskDialog(task, position));
        }

        private void applyTaskStyle(TaskViewHolder holder, boolean isDone) {
            if (isDone) {
                holder.tvTaskName.setPaintFlags(holder.tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvTaskName.setTextColor(Color.parseColor("#AAAAAA"));
            } else {
                holder.tvTaskName.setPaintFlags(holder.tvTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.tvTaskName.setTextColor(Color.parseColor("#333333"));
            }
        }

        private void showEditTaskDialog(Task task, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PomodoroActivity.this);
            builder.setTitle("Sửa công việc");

            final EditText input = new EditText(PomodoroActivity.this);
            input.setText(task.name);
            builder.setView(input);

            builder.setPositiveButton("Cập nhật", (dialog, which) -> {
                task.name = input.getText().toString().trim();
                notifyItemChanged(position);
                updateCurrentTaskLabel();
            });
            builder.setNegativeButton("Xóa", (dialog, which) -> {
                tasks.remove(position);
                notifyItemRemoved(position);
                updateCurrentTaskLabel();
            });
            builder.show();
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        class TaskViewHolder extends RecyclerView.ViewHolder {
            CheckBox cbDone;
            TextView tvTaskName;
            ImageView btnEditTask;

            TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                cbDone = itemView.findViewById(R.id.cbDone);
                tvTaskName = itemView.findViewById(R.id.tvTaskName);
                btnEditTask = itemView.findViewById(R.id.btnEditTask);
            }
        }
    }
}
