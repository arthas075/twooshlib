package lib.twoosh.twooshlib.models;

/**
 * Created by arthas on 18/9/16.
 */
public class PeopleListItem {



    public String name;
    public String userid;
    public String pic;
    public String email;
    public String mobile;
    public String gender;
    public String dob;
    public String corp_id;
    public String corp_appname;
    public String taglist;
    public String primary_tag;



    public PeopleListItem(String name,String pic, String email,String mobile, String gender,String dob, String corp_id, String corp_appname, String taglist, String userid )
    {
        this.name = name;
        this.pic = pic;
        this.email = email;
        this.mobile = mobile;
        this.gender = gender;
        this.dob = dob;
        this.corp_id = corp_id;
        this.corp_appname = corp_appname;
        this.taglist = taglist;
        this.userid = userid;

    }

    public PeopleListItem()
    {

    }
}
