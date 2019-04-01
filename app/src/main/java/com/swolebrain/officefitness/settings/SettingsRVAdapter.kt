package com.swolebrain.officefitness.settings

import android.app.Fragment
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.ui.auth.data.model.User
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.loadProfileDataFromFireBase
import com.swolebrain.officefitness.repositories.UserProfile
import com.swolebrain.officefitness.repositories.loadUserProfileData
import kotlinx.android.synthetic.main.fragment_settings_rv_clan_input.view.*
import kotlinx.android.synthetic.main.fragment_settings_rv_clan_row.view.*
import kotlinx.android.synthetic.main.fragment_settings_rv_display_name_select.view.*
import kotlinx.android.synthetic.main.fragment_settings_rv_gender_select.view.*
import kotlinx.android.synthetic.main.fragment_settings_rv_title.view.*

class SettingsRVAdapter(val c: Context, val fragment :SettingsFragment) : RecyclerView.Adapter<SettingsRVAdapter.BaseSettingsViewHolder>(){
    companion object {
        val TITLE_VIEW = 0
        val DISPLAY_NAME_SELECT_VIEW = 1
        val GENDER_SELECT_VIEW = 2
        val CLAN_ROW_VIEW = 3
        val CLAN_ENTRY_VIEW = 4
    }
    private lateinit var userProfile: UserProfile
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSettingsViewHolder {

        val inflater : LayoutInflater = LayoutInflater.from(c)
        return when (viewType){
            TITLE_VIEW -> BaseSettingsViewHolder(inflater.inflate(R.layout.fragment_settings_rv_title, parent, false))
            DISPLAY_NAME_SELECT_VIEW -> EditDisplayNameViewHolder(
                    inflater.inflate(R.layout.fragment_settings_rv_display_name_select, parent, false),
                    this.fragment,
                    userProfile
            )
            GENDER_SELECT_VIEW -> GenderSelectViewHolder(
                    inflater.inflate(R.layout.fragment_settings_rv_gender_select, parent, false),
                    this.fragment
            )
            CLAN_ROW_VIEW -> ClanRowViewHolder(
                    inflater.inflate(R.layout.fragment_settings_rv_clan_row, parent, false),
                    this.fragment
            )
            CLAN_ENTRY_VIEW -> JoinClanViewHolder(
                    inflater.inflate(R.layout.fragment_settings_rv_clan_input, parent, false),
                    this.fragment
            )
            else -> throw IllegalArgumentException("Settings recyclerview got unexpected view type")
        }
    }


    override fun getItemCount(): Int = if (::userProfile.isInitialized) userProfile.clansList.size + 5 else 5

    override fun onBindViewHolder(holder: BaseSettingsViewHolder, position: Int) {
        when (getItemViewType(position)){
            TITLE_VIEW -> holder.itemView.label_title.text = when (position) {
                0 -> "Avatar"
                3 -> "Clans"
                else -> "Err"
            }
            DISPLAY_NAME_SELECT_VIEW -> {
                if (::userProfile.isInitialized){
                    if(!userProfile.displayName.isBlank())
                         holder.itemView.display_name_input.setText(userProfile.displayName)
                    else
                         holder.itemView.display_name_input.setText ( userProfile.userName )

                    if (!(holder as EditDisplayNameViewHolder).isInitialized) holder.bindHandlers()
                }
            }
            GENDER_SELECT_VIEW -> {
                if (::userProfile.isInitialized){
                    holder.itemView.radio_male.isChecked = userProfile.gender == "male"
                    holder.itemView.radio_female.isChecked = userProfile.gender == "female"
                    if (!(holder as GenderSelectViewHolder).isInitialized) holder.bindHandlers()
                }
            }
            CLAN_ROW_VIEW -> {
                if (::userProfile.isInitialized && userProfile.clansList.isNotEmpty()){
                    holder.itemView.tv_clan_name.text = userProfile.clansList[position-4]
                    if (!(holder as ClanRowViewHolder).isInitialized) holder.bindHandlers()
                }
            }
            CLAN_ENTRY_VIEW -> {
                if (::userProfile.isInitialized){
                    if (!(holder as JoinClanViewHolder).isInitialized) holder.bindHandlers()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val lastClanEntryIndex:Int = if (::userProfile.isInitialized) userProfile.clansList.size + 3 else 3
        return when (position){
            0 -> TITLE_VIEW
            1 -> DISPLAY_NAME_SELECT_VIEW
            2 -> GENDER_SELECT_VIEW
            3 -> TITLE_VIEW
            in 4..lastClanEntryIndex -> CLAN_ROW_VIEW
            lastClanEntryIndex+1 -> CLAN_ENTRY_VIEW
            else -> throw IllegalArgumentException("Settings recyclerview got unexpected view type")
        }
    }

    fun setProfile(profile: UserProfile?){
        if (profile != null) userProfile = profile
        notifyDataSetChanged()
    }

    open class BaseSettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class EditDisplayNameViewHolder(itemView: View, val fragment: SettingsFragment, val userProfile: UserProfile) : BaseSettingsViewHolder(itemView){
        var isInitialized = false
        fun bindHandlers(){
            itemView.display_name_input.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if(s != null){
                        if( s.isBlank()){
                            s.insert(0, userProfile.userName)
                        }
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                     fragment.commitChanges("displayName", s.toString(), null)
                }

            })
            isInitialized = true
        }
    }

    class GenderSelectViewHolder(itemView: View, val fragment: SettingsFragment) : BaseSettingsViewHolder(itemView){
        var isInitialized = false
        fun bindHandlers(){
            itemView.radio_male.setOnClickListener { fragment.commitChanges("gender", "male", null) }
            itemView.radio_female.setOnClickListener { fragment.commitChanges("gender", "female", null) }
            isInitialized = true
        }
    }

    class JoinClanViewHolder(itemView: View, val fragment: SettingsFragment) : BaseSettingsViewHolder(itemView){
        var isInitialized = false
        var isSubmitting = false
        fun bindHandlers(){

            itemView.et_join_clan.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            itemView.et_join_clan.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(s: Editable?) {
                    if( itemView.et_join_clan.text.isBlank() ) {
                        itemView.til_join_clan.error = "Please enter a clan name"
                    }else
                        itemView.til_join_clan.error = null
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            itemView.btn_join_clan.setOnClickListener {

                // validates blank data
                if( itemView.et_join_clan.text.isBlank() )  return@setOnClickListener

                if (isSubmitting) return@setOnClickListener
                isSubmitting = true
                Toast.makeText(fragment.activity, "It may take a minute for clan rankings to update", Toast.LENGTH_LONG)
                fragment.commitChanges("clans", itemView.et_join_clan.text.toString(), "add")?.addOnSuccessListener {
                    itemView.et_join_clan.setText("")
                    isSubmitting = false
                }
            }
            isInitialized = true
        }
    }

    class ClanRowViewHolder(itemView: View, val fragment: SettingsFragment) : BaseSettingsViewHolder(itemView){
        var isInitialized = false
        fun bindHandlers(){
            itemView.iv_leave_clan.setOnClickListener{
                fragment.commitChanges("clans", itemView.tv_clan_name.text.toString(), "remove")
            }
        }
    }
}