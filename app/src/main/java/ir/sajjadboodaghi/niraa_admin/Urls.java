package ir.sajjadboodaghi.niraa_admin;

/**
 * Created by Sajjad on 02/10/2018.
 */

public class Urls {
    public static final String BASE_URL          = "http://sajjadboodaghi.ir/niraa/";

    public static final String ADMIN_SRC_URL     = BASE_URL + "admin/";
    public static final String IMAGES_BASE_URL   = BASE_URL + "client/images/";
    public static final String STORIES_BASE_URL  = BASE_URL + "client/stories/";
    public static final String GET_CATEGORY      = BASE_URL + "client/categories.php";

    public static final String GET_WAITING_ITEMS = ADMIN_SRC_URL + "get_waiting_items.php";
    public static final String LOGIN_ADMIN       = ADMIN_SRC_URL + "login_admin.php";
    public static final String VERIFY_ITEM       = ADMIN_SRC_URL + "verify_item.php";
    public static final String SAVE_NEW_CATEGORY = ADMIN_SRC_URL + "save_new_category.php";
    public static final String SAVE_NEW_PLACE    = ADMIN_SRC_URL + "save_new_place.php";
    public static final String UPDATE_ITEM       = ADMIN_SRC_URL + "update_item.php";
    public static final String DELETE_ITEM       = ADMIN_SRC_URL + "delete_item.php";
    public static final String GET_USER_ITEMS    = ADMIN_SRC_URL + "get_user_items.php";
    public static final String BLOCK_USER        = ADMIN_SRC_URL + "block_user.php";
    public static final String GET_SUGGESTS      = ADMIN_SRC_URL + "get_suggests.php";
    public static final String DELETE_SUGGEST    = ADMIN_SRC_URL + "delete_suggest.php";
    public static final String GET_REPORTS       = ADMIN_SRC_URL + "get_reports.php";
    public static final String DELETE_REPORT     = ADMIN_SRC_URL + "delete_report.php";
    public static final String GET_ITEM          = ADMIN_SRC_URL + "get_item.php";
    public static final String GET_USERS         = ADMIN_SRC_URL + "get_users.php";
    public static final String WHATSUP           = ADMIN_SRC_URL + "whatsup.php";
    public static final String DELETE_STORY      = ADMIN_SRC_URL + "delete_story.php";
    public static final String EXPIRE_STORIES_24 = ADMIN_SRC_URL + "expire_stories_24.php";
    public static final String DELETE_STORIES_7  = ADMIN_SRC_URL + "delete_stories_7.php";
    public static final String SEND_BROADCAST    = ADMIN_SRC_URL + "send_broadcast.php";
    public static final String GET_STORIES       = BASE_URL + "/client/v3/get_stories.php";
}