package com.example.holic.basecalendar

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.holic.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_schedule.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_schedule.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var scheduleRecyclerViewAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var weekLinearLayout = findViewById<LinearLayout>(R.id.weekLinearLayout)
        var menuLinearLayout = findViewById<LinearLayout>(R.id.menuLinearLayout)

        initView()



        //색상변경
        var colorPreference =""

        if(savedInstanceState==null){
            var prefs : SharedPreferences = getSharedPreferences("color_info", 0)
            var colorPreference = prefs.getString("color", "#e0e7ee")
            weekLinearLayout.setBackgroundColor(Color.parseColor(colorPreference))
            menuLinearLayout.setBackgroundColor(Color.parseColor(colorPreference))
        }

        var setColorImage = findViewById<ImageView>(R.id.setColorImage)
        setColorImage.setOnClickListener {
            var items = arrayOf("default", "red", "yellow", "green", "blue", "violet", "pink")

            var builder = AlertDialog.Builder(this)
            builder.setTitle("테마 변경")
            builder.setIcon(R.drawable.palette)
            builder.setSingleChoiceItems(items, -1,  { _, which->
                var prefs : SharedPreferences = getSharedPreferences("color_info", 0)
                var editor : SharedPreferences.Editor = prefs.edit()

                if(items[which].equals("default")) {
                    weekLinearLayout.setBackgroundColor(Color.parseColor("#e0e7ee"))
                    menuLinearLayout.setBackgroundColor(Color.parseColor("#e0e7ee"))
                    editor.putString("color","#e0e7ee" )
                }
                if(items[which].equals("red")){
                    weekLinearLayout.setBackgroundColor(Color.parseColor("#D89090"))
                    menuLinearLayout.setBackgroundColor(Color.parseColor("#D89090"))
                    editor.putString("color","#D89090" )
                }
                if(items[which].equals("blue")){
                    weekLinearLayout.setBackgroundColor(Color.parseColor("#B5E3D5"))
                    menuLinearLayout.setBackgroundColor(Color.parseColor("#B5E3D5"))
                    editor.putString("color","#B5E3D5" )
                }
                if(items[which].equals("green")){
                    weekLinearLayout.setBackgroundColor(Color.parseColor("#E0DC96"))
                    menuLinearLayout.setBackgroundColor(Color.parseColor("#E0DC96"))
                    editor.putString("color","#E0DC96" )
                }
                if(items[which].equals("violet")){
                    weekLinearLayout.setBackgroundColor(Color.parseColor("#CCC6E6"))
                    menuLinearLayout.setBackgroundColor(Color.parseColor("#CCC6E6"))
                    editor.putString("color","#CCC6E6" )
                }
                if(items[which].equals("yellow")){
                    weekLinearLayout.setBackgroundColor(Color.parseColor("#EED39A"))
                    menuLinearLayout.setBackgroundColor(Color.parseColor("#EED39A"))
                    editor.putString("color","#EED39A" )
                }
                if(items[which].equals("pink")){
                    weekLinearLayout.setBackgroundColor(Color.parseColor("#EFB7B7"))
                    menuLinearLayout.setBackgroundColor(Color.parseColor("#EFB7B7"))
                    editor.putString("color","#EFB7B7" )
                }
                editor.apply()
            })

            val AlertDialog = builder.create()
            AlertDialog.show()
        } //여기까지 색상변경

        //+버튼
        var plusButton = findViewById<ImageView>(R.id.addButton)
        plusButton.setOnClickListener{
            //addDialog()
            val Intent = Intent(this, PhotoMemoActivity::class.java)
            startActivity(Intent)
        }

    }

    fun initView() {

        scheduleRecyclerViewAdapter = RecyclerViewAdapter(this)

        recyclerViewSchedule.layoutManager = GridLayoutManager(this, BaseCalendar.DAYS_OF_WEEK)
        recyclerViewSchedule.adapter = scheduleRecyclerViewAdapter
        //recyclerViewSchedule.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
        recyclerViewSchedule.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        previousMonthButton.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToPrevMonth()
        }

        nextMonthButton.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToNextMonth()
        }
    }

    fun refreshCurrentMonth(calendar: Calendar) {
        val sdf = SimpleDateFormat("yyyy년 MM월", Locale.KOREAN)
        textViewCurrentMonth.text = sdf.format(calendar.time)
    }

    /*
    // 메뉴바 설정
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    // 메뉴바 '+ 아이콘' 클릭 시
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        when (item.itemId){
            // 일정(다중 선택) 등록 및 사진 메모 추가
            R.id.addIcon -> {
                addDialog()
                return true
            }
            // 색상 테마 변경
            R.id.settingIcon -> {
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
    */

    /*
    fun addDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.photo_memo_custom_dialog, null)

        val dialogPicture = dialogView.findViewById<ImageView>(R.id.selectedPictureImageView)
        val dialogMemo = dialogView.findViewById<EditText>(R.id.memoEditText)

        databaseReference.child("memo1").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                val children = datasnapshot.children
                children.forEach {
                    val memo = datasnapshot.getValue().toString()
                    // dialogMemo setText
                    Log.v("exist", memo)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("error", "onCancelled: " + error.message)
            }
        })

        builder.setView(dialogView)
               .setPositiveButton("확인") { dialogInterface, i ->

                    // 확인일 때 main의 View의 값에 dialog View에 있는 값을 적용
               }
                .setNegativeButton("취소") { dialogInterface, i ->
                   // 취소일 때 아무 액션이 없으므로 빈칸
               }
              .show()
        }
    */
}

