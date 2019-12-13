package com.example.holic.basecalendar

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.holic.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_schedule.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_show_schedule.view.*
import kotlinx.android.synthetic.main.item_schedule.*
import java.text.SimpleDateFormat
import java.util.*

class RecyclerViewAdapter(val mainActivity: MainActivity) : RecyclerView.Adapter<ViewHolderHelper>() {

    val baseCalendar = BaseCalendar()

    //weather
    var result = WeatherTask().execute().get()
    var spl2 = result.substring(1,result.length-1)
    var spl = spl2.split(", ")
    var c = Calendar.getInstance()

    init {
        baseCalendar.initBaseCalendar {
            refreshView(it)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ViewHolderHelper(view)
    }

    override fun getItemCount(): Int {
        return BaseCalendar.LOW_OF_CALENDAR * BaseCalendar.DAYS_OF_WEEK
    }

    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {

        if (position % BaseCalendar.DAYS_OF_WEEK == 0) holder.dateTextView.setTextColor(Color.parseColor("#ff1200"))
        else holder.dateTextView.setTextColor(Color.parseColor("#676d6e"))

        if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
            holder.dateTextView.alpha = 0.3f //이번달 아니면
        } else {
            holder.dateTextView.alpha = 1f // 이번달이면
        }
        holder.dateTextView.text = baseCalendar.data[position].toString()

        if(position==8)
            holder.pictureImageView?.setImageResource(R.drawable.picture1)
        if(position==6)
            holder.pictureImageView?.setImageResource(R.drawable.picture2)
        if(position==2)
            holder.pictureImageView?.setImageResource(R.drawable.picture3)
        if(position==25)
            holder.pictureImageView?.setImageResource(R.drawable.picture4)

        if (baseCalendar.sdf!="201912")
            holder.pictureImageView?.setImageResource(0)


        //데베 가져와서 창에 띄우기
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("user")//.child("schedule2")
        val countRef = database.getReference("saveCount")

        var saveId : String = "schedule"


        //일정 등록
        holder.dateTextView.setOnClickListener {
            var dialogView = LayoutInflater.from(holder.dateTextView.context).inflate(R.layout.activity_add_schedule, null)

            var builder = AlertDialog.Builder(holder.dateTextView.context).setView(dialogView).setTitle("일정 등록")
            //show dialog
            val AlertDialog = builder.create()
            AlertDialog.show()

            //id설정을 위한 count가져오기
            var count : String =""


            countRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var nowSdf : String =""
                    if(position<9) { //nowSdf와 id값 비교
                        nowSdf = ((baseCalendar.sdf + "0" + (position + 1).toString()).toInt()-baseCalendar.prevMonthTailOffset).toString()
                    }
                    else {
                        nowSdf = ((baseCalendar.sdf + (position + 1).toString()).toInt()-baseCalendar.prevMonthTailOffset).toString()
                    }

                    count = dataSnapshot.value!!.toString() //3
                    Log.d("heo", count)

                    saveId= saveId + (count.toInt()+1).toString() //schedule4
                    Log.d("heo", saveId)
                    Log.d("heo",saveId.substring(8,9))
                    dialogView.button_confirm.setOnClickListener {
                        //값저장
                        val add_schedule = dialogView.editText_Calendar_Add.text.toString()
                        val add_location = dialogView.editText_Location.text.toString()
                        val add_Time = dialogView.timePicker_Time
                        Log.d("heo",add_schedule)
                        Log.d("heo",add_location)
                        add_Time.setIs24HourView(true)
                        //입력한 시간과 분 가져오기
                        var schedule_Time : String=""
                        if(add_Time.minute<10)
                            schedule_Time = add_Time.hour.toString() + ":0" + add_Time.minute.toString()
                        else
                            schedule_Time = add_Time.hour.toString() + ":" + add_Time.minute.toString()
                        Log.d("heo",schedule_Time)
                        Log.v("seyuuun", schedule_Time)

                        myRef.child(saveId).child("id").setValue(nowSdf)
                        myRef.child(saveId).child("title").setValue(add_schedule)
                        myRef.child(saveId).child("place").setValue(add_location)
                        myRef.child(saveId).child("time").setValue(schedule_Time)
                        countRef.setValue(saveId.substring(8,9))
                        saveId="schedule"

                        AlertDialog.dismiss()
                    }


                    //취소버튼 눌렀을때
                    dialogView.button_cancel.setOnClickListener {
                        AlertDialog.cancel()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w("heo", "Failed to read value.", error.toException())
                }
            })

        }


        //날씨처리
        if(baseCalendar.nowMonth==(c.get(Calendar.MONTH)+1).toString()) {
            if (baseCalendar.data[position].toString().equals((c.get(Calendar.DATE)).toString()) && baseCalendar.currentMonthMaxDate.toString().equals(
                    (baseCalendar.data.size - baseCalendar.nextMonthHeadOffset).toString()))
                holder.imageWeather?.setImageResource(spl[0].toInt())
            if (baseCalendar.data[position].toString().equals((c.get(Calendar.DATE) + 1).toString()) &&
                baseCalendar.currentMonthMaxDate.toString().equals((baseCalendar.data.size - baseCalendar.nextMonthHeadOffset).toString()))
                holder.imageWeather?.setImageResource(spl[1].toInt())
            if (baseCalendar.data[position].toString().equals((c.get(Calendar.DATE) + 2).toString()) &&
                baseCalendar.currentMonthMaxDate.toString().equals((baseCalendar.data.size - baseCalendar.nextMonthHeadOffset).toString()))
                holder.imageWeather?.setImageResource(spl[2].toInt())
            if (baseCalendar.data[position].toString().equals((c.get(Calendar.DATE) + 3).toString()) &&
                baseCalendar.currentMonthMaxDate.toString().equals((baseCalendar.data.size - baseCalendar.nextMonthHeadOffset).toString()))
                holder.imageWeather?.setImageResource(spl[3].toInt())
            if (baseCalendar.data[position].toString().equals((c.get(Calendar.DATE) + 4).toString()) &&
                baseCalendar.currentMonthMaxDate.toString().equals((baseCalendar.data.size - baseCalendar.nextMonthHeadOffset).toString()))
                holder.imageWeather?.setImageResource(spl[4].toInt())
        }
            if (position>baseCalendar.currentMonthMaxDate)
                holder.imageWeather?.setImageResource(0)



        //클릭하면 상세화면 나오게
        holder.pictureImageView.setOnClickListener {
            var builder = AlertDialog.Builder(holder.itemLinearLayout.context)

            var id: String = "schedule"
            for (n in 1 until 50) {
                id += n.toString()//찾을 id 설정 (schedule1 schedule2 schedule3 이렇게
                var myRefChild = myRef.child(id)

                myRefChild.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var nowSdf: String = ""
                        if (position < 9) { //nowSdf와 id값 비교
                            nowSdf =
                                ((baseCalendar.sdf + "0" + (position + 1).toString()).toInt() - baseCalendar.prevMonthTailOffset).toString()
                        } else {
                            nowSdf =
                                ((baseCalendar.sdf + (position + 1).toString()).toInt() - baseCalendar.prevMonthTailOffset).toString()
                        }

                        var children = myRef.child("")
                        for (snapshot in dataSnapshot.children) {
                            if (snapshot.key == "id") {
                                if (snapshot.value.toString().equals(nowSdf)) {
                                    var dialogView = LayoutInflater.from(holder.dateTextView.context)
                                        .inflate(R.layout.activity_show_schedule, null)

                                    dialogView.textView_schedule.setText(dataSnapshot.child("title").value!!.toString())
                                    dialogView.textView_Location.setText(dataSnapshot.child("place").value!!.toString())
                                    dialogView.textView_Time.setText(dataSnapshot.child("time").value!!.toString())
                                    //dialogView.imageView2_picture.setImage

                                    var builder = AlertDialog.Builder(holder.dateTextView.context).setView(dialogView)
                                        .setTitle("${nowSdf.substring(0, 4)}년 ${nowSdf.substring(
                                            4,
                                            6
                                        )}월 ${nowSdf.substring(6, 8)}일 일정")
                                    //show dialog
                                    val AlertDialog = builder.create()
                                    AlertDialog.show()

                                    //확인버튼눌렀을때
                                    dialogView.button_schedule_confirm.setOnClickListener {
                                        AlertDialog.dismiss()
                                    }
                                    //취소버튼눌렀을때
                                    dialogView.button_schedule_cancel.setOnClickListener {
                                        AlertDialog.cancel()
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("heo", "Failed to read value.", error.toException())
                    }
                })
                id = "schedule" //다음에 다시 가져오기 위해 id를 schedule로 초기화
            }
        }


        //일정 있으면 위에 dot
        var id : String = "schedule"
        for(n in 1 until 50){
            id+=n.toString()//찾을 id 설정
            var myRefChild = myRef.child(id)

            myRefChild.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var nowSdf : String =""
                    if(position<9) {
                        nowSdf = ((baseCalendar.sdf + "0" + (position + 1).toString()).toInt()-baseCalendar.prevMonthTailOffset).toString()
                    }
                    else {
                        nowSdf = ((baseCalendar.sdf + (position + 1).toString()).toInt()-baseCalendar.prevMonthTailOffset).toString()
                    }

                    var children = myRef.child("")
                    for (snapshot in dataSnapshot.children) {
                        if (snapshot.key == "id"){
                            if(snapshot.value.toString().equals(nowSdf)){
                                holder.dateImageView?.setImageResource(R.drawable.dot)
                                holder.scheduleTextView?.setText(dataSnapshot.child("title").value!!.toString())
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("heo", "Failed to read value.", error.toException())
                }
            })
            id="schedule"
        }


    }

    fun changeToPrevMonth() {
        baseCalendar.changeToPrevMonth {
            refreshView(it)
        }
    }

    fun changeToNextMonth() {
        baseCalendar.changeToNextMonth {
            refreshView(it)
        }
    }

    private fun refreshView(calendar: Calendar) {
        notifyDataSetChanged()
        mainActivity.refreshCurrentMonth(calendar)
    }
}