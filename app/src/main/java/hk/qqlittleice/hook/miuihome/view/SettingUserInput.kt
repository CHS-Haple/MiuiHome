package hk.qqlittleice.hook.miuihome.view

import android.app.AlertDialog
import android.graphics.Color
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import hk.qqlittleice.hook.miuihome.HomeContext
import hk.qqlittleice.hook.miuihome.utils.LogUtil
import hk.qqlittleice.hook.miuihome.utils.OwnSP
import hk.qqlittleice.hook.miuihome.utils.dp2px
import hk.qqlittleice.hook.miuihome.utils.isNightMode

class SettingUserInput(private val mText: String, private val mKey: String, private val minValue: Int, private val maxValue: Int,
                       private val divide: Int = 100, private val defval:Int) {

    private val sharedPreferences = OwnSP.ownSP
    private val editor by lazy { sharedPreferences.edit() }

    fun build(): AlertDialog {
        lateinit var editText: EditText
        val dialogBuilder = SettingBaseDialog().get()
        dialogBuilder.setView(ScrollView(HomeContext.activity).apply {
            overScrollMode = 2
            addView(LinearLayout(HomeContext.activity).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp2px(HomeContext.context, 20f), dp2px(HomeContext.context, 10f), dp2px(HomeContext.context, 20f), dp2px(HomeContext.context, 5f))
                addView(SettingTextView.FastBuilder(mText = "请输入[${mText}]的值：", mSize = SettingTextView.textSize).build())
                addView(EditText(HomeContext.context).apply {
                    editText = this
                    inputType = EditorInfo.TYPE_CLASS_NUMBER
                    setTextColor(Color.parseColor(if (isNightMode(getContext())) "#ffffff" else "#000000"))
                })
                addView(SettingTextView.FastBuilder(mText = "官方默认值：$defval").build())
                addView(SettingTextView.FastBuilder(mText = "可输入范围：$minValue~$maxValue").build())
                addView(SettingTextView.FastBuilder(mText = "该输入值会被除以$divide").build())
            })
        })
        dialogBuilder.apply {
            setPositiveButton("保存", null)
            setNeutralButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
        }
        dialogBuilder.show().apply {
            this.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                try {
                    if (saveValue(editText.text.toString().toFloat() / divide)) {
                        LogUtil.toast("[$mText]设置成功")
                        this.dismiss()
                    }
                } catch (e: NumberFormatException) {
                    LogUtil.toast("请输入正确的值！")
                }
            }
            return this
        }
    }

    private fun saveValue(value: Float): Boolean {
        if ((value < (minValue.toFloat() / divide)) or (value > (maxValue.toFloat() / divide))) {
            LogUtil.toast("输入的值大于或小于允许设定的值！")
            return false
        }
        editor.putFloat(mKey, value)
        editor.apply()
        return true
    }

}