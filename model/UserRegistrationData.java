import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by isak.prytz on 12/02/15 Week: 7.
 */
public class UserRegistrationData implements Parcelable {

    @SerializedName("zip_code")
    private String mZipCode;
    @SerializedName("state")
    private String mState;
    @SerializedName("region")
    private String mRegion;
    @SerializedName("gender")
    private int mGender;
    @SerializedName("birth_date")
    private long mBirthDate;
    @SerializedName("job")
    private int mJob;
    @SerializedName("child_number")
    private int mChildNumber;
    @SerializedName("child_birth_date")
    private long mChildBirthDate;
    @SerializedName("coupon_news")
    private boolean mCouponNews;
    @SerializedName("gift_news")
    private boolean mGiftNews;
    @SerializedName("product_news")
    private boolean mProductNews;

    public UserRegistrationData(String zipCode,
                                String state,
                                String region,
                                int gender,
                                Date birthDate,
                                int job,
                                int childNumber,
                                Date childBirthDate,
                                boolean couponNews,
                                boolean giftNews,
                                boolean productNews) {

        mZipCode = zipCode;
        mState = state;
        mRegion = region;
        mGender = gender;
        mBirthDate = (birthDate != null ? birthDate.getTime() : -1L);
        mJob = job;
        mChildNumber = childNumber;
        mChildBirthDate = (childBirthDate != null ? childBirthDate.getTime() : -1L);
        mCouponNews = couponNews;
        mGiftNews = giftNews;
        mProductNews = productNews;

    }

    public UserRegistrationData(Parcel in) {

        mZipCode = in.readString();
        mState = in.readString();
        mRegion = in.readString();
        mGender = in.readInt();
        mBirthDate = in.readLong();
        mJob = in.readInt();
        mChildNumber = in.readInt();
        mChildBirthDate = in.readLong();
        mCouponNews = (in.readInt() != 0); //boolean = true if int != 0
        mGiftNews = (in.readInt() != 0);
        mProductNews = (in.readInt() != 0);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mZipCode);
        dest.writeString(mState);
        dest.writeString(mRegion);
        dest.writeInt(mGender);
        dest.writeLong(mBirthDate);
        dest.writeInt(mJob);
        dest.writeInt(mChildNumber);
        dest.writeLong(mChildBirthDate);
        dest.writeInt((mCouponNews) ? 1 : 0); //if boolean == true, int = 1
        dest.writeInt((mGiftNews) ? 1 : 0);
        dest.writeInt((mProductNews) ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getZipCode() {
        return mZipCode;
    }

    public String getState() {
        return mState;
    }

    public String getRegion() {
        return mRegion;
    }

    public int getGender() {
        return mGender;
    }

    public Date getBirthDate() {
        if(mBirthDate == -1L){
            return null;
        }
        return new Date(mBirthDate);
    }

    public int getJob() {
        return mJob;
    }

    public int getChildNumber() {
        return mChildNumber;
    }

    public Date getChildBirthDate() {
        if(mChildBirthDate == -1L){
            return null;
        }
        return new Date(mChildBirthDate);
    }

    public boolean getCouponNews() {
        return mCouponNews;
    }

    public boolean getGiftNews() {
        return mGiftNews;
    }

    public boolean getProductNews() {
        return mProductNews;
    }

    @Override
    public String toString() {
        return "Zip Code : " + mZipCode + "\n" +
                "State : " + mState + "\n" +
                "Region : " + mRegion + "\n" +
                "Gender : " + mGender + "\n" +
                "Birth Date : " + new Date(mBirthDate).toString() + "\n" +
                "Job : " + mJob + "\n" +
                "Child Number : " + mChildNumber + "\n" +
                "Child Birth Date : " + new Date(mChildBirthDate).toString() + "\n" +
                "Coupon News : " + mCouponNews + "\n" +
                "Gift News : " + mGiftNews + "\n" +
                "Product News : " + mProductNews + "\n";
    }

}
