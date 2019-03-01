package com.moxi.bookstore.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/10/10.
 */
public class PromotionPrams implements Parcelable {

    private String code;

    private String type;

    public void setCode(String code){
        this.code = code;
    }
    public String getCode(){
        return this.code;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.type);
    }

    public PromotionPrams() {
    }

    protected PromotionPrams(Parcel in) {
        this.code = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<PromotionPrams> CREATOR = new Parcelable.Creator<PromotionPrams>() {
        @Override
        public PromotionPrams createFromParcel(Parcel source) {
            return new PromotionPrams(source);
        }

        @Override
        public PromotionPrams[] newArray(int size) {
            return new PromotionPrams[size];
        }
    };
}
