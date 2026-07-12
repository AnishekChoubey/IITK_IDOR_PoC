import com.google.gson.annotations.SerializedName;

import java.util.function.Consumer;
public class UserDatIITK {




    @SerializedName("applicant_name")
    public String applicant_name;

    @SerializedName("mobile_no")
     public String mobile_no;

    @SerializedName("email_id")
     public String email_id;


    @Override
    public String toString() {
        return "UserDatIITK{" +
                " applicant_name='" + applicant_name + '\'' +
                ", mobile_no='" + mobile_no + '\'' +
                ", email_id='" + email_id + '\'' +
                '}';
    }
}
