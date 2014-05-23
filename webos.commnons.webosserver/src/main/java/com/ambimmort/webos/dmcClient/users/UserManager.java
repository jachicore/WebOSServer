package com.ambimmort.webos.dmcClient.users;

import com.ambimmort.webos.dmcClient.filesystem.concrete.FileSystem;
import com.ambimmort.webos.dmcClient.registry.Registry;
import com.ambimmort.webos.commons.dmc.IHttpSessionAware;
import com.ambimmort.webos.dmcClient.app.App;
import com.ambimmort.webos.dmcClient.filesystem.concrete.METAFileSystem;
import com.ambimmort.webos.dmcClient.filesystem.elements.FileSystemDB;
import com.ambimmort.webos.dmcClient.filesystem.utils.FSConfig;
import javax.servlet.http.HttpSession;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class UserManager
        implements IHttpSessionAware {

    HttpSession session;
    Registry reg = new Registry();
    FileSystem fs;

    public UserManager() {
        this.session = null;
        checkInit();
    }

    private void checkInit() {
        if (UserDB.getInstance().getUser("admin") == null) {
            UserDB.getInstance().addUser("admin", "{\"password\":\"1234\",\"phone\":\"\",\"telephone\":\"\",\"fax\":\"\",\"qq\":\"\",\"email\":\"\",\"address\":\"\",\"introduction\":\"\",\"name\":\"admin\",\"department\":\"\"}", "[\"admin\"]");
            UserDB.getInstance().addGroup("admin");
            UserDB.getInstance().alterUserGroup("admin", "[\"admin\"]");
            Registry r = new Registry();
            r.addRegistry("admin_sysconfig", "{\"currentSkin\":\"css/black2.css\",\"currentWallpaper\":\"/images/theme/wallpapers/7.jpg\",\"currentDesk\":\"default\"}");
            App app = new App();
            String id = app.addInnerApp("用户组管理", "",
                    "{\"image\": \"images/icons/icon-04-1.png\", \"winSettings\":{\"width\": 700, \"height\":700, \"title\":\"用户组管理\", \"minimizeIcon\":\"images/icons/icon-04-1.png\"},\"funcs\": [{\"id\": \"1000\",\"name\": \"用户组管理\",\"url\":\"system/Settings/users/groupManager/group-app.html\"},{\"id\": \"1\",\"name\": \"用户组管理\",\"url\":\"\"}]}"
            );
            if (id != null) {
                UserDB.getInstance().addGroupPermission("admin", id, "[{\"id\":\"1\",\"name\":\"用户组管理\"}]");
            }
            id = app.addInnerApp("用户管理", "",
                    "{\"image\": \"images/icons/user_add_icon.png\", \"winSettings\":{\"width\": 700, \"height\":700, \"title\":\"用户管理\", \"minimizeIcon\":\"images/icons/user_add_icon.png\"},\"funcs\": [{\"id\": \"1000\",\"name\": \"用户管理\",\"url\":\"system/Settings/users/userManager/user-list.html\"},{\"id\": \"1\",\"name\": \"用户管理\",\"url\":\"\"}]}"
            );
            if (id != null) {
                UserDB.getInstance().addGroupPermission("admin", id, "[{\"id\":\"1\",\"name\":\"用户管理\"}]");
            }
            id = app.addInnerApp("应用程序中心", "",
                    "{\"image\": \"images/icons/icon-appshop.png\", \"winSettings\":{\"width\": 700, \"height\":700, \"title\":\"应用程序中心\", \"minimizeIcon\":\"images/icons/icon-appshop.png\"},\"funcs\": [{\"id\": \"1000\",\"name\": \"应用程序中心\",\"url\":\"system/AppCenter/index.html\"},{\"id\": \"1\",\"name\": \"应用程序中心\",\"url\":\"\"}]}"
            );
            if (id != null) {
                UserDB.getInstance().addGroupPermission("admin", id, "[{\"id\":\"1\",\"name\":\"应用程序中心\"}]");
            }
            for (String dir : FSConfig.getInstance().getSysfiles()) {
                //FileSystemDB.getInstance().
                METAFileSystem fstemp = new METAFileSystem("admin");
                fstemp.mkdir(dir);
            }
        }
    }

    public void bind(HttpSession session) {
        this.session = session;
        if (session.getAttribute("username") == null) {
            return;
        }

        fs = new FileSystem();
        fs.injectSession(session);

    }

    public String getUser(String username) {
        return JSONObject.fromObject(UserDB.getInstance().getUser(username)).toString();
    }

    public String saveUser(String username, String content, String roles) {
        User user = new User();
        user.setUsername(username);
        user.setProfile(content);
        reg.addRegistry(username + "_sysconfig", "{\"currentSkin\":\"css/black2.css\",\"currentWallpaper\":\"/images/theme/wallpapers/7.jpg\",\"currentDesk\":\"default\"}");
        String rst = UserDB.getInstance().addUser(username, content, roles);
        return rst;
    }

    public String getUserList() {
        return JSONArray.fromObject(UserDB.getInstance().getUsers()).toString();

    }

    public String login(String username, String password) {
        //String test = ((User) UserDB.getInstance().getUsers().get(username)).getProfile();
        User user = UserDB.getInstance().getUser(username);
        if (user == null) {
            return "用户不存在";
        }
        JSONObject profile = JSONObject.fromObject(user.getProfile());
        return ((profile.getString("password").equals(password)) ? "true" : "false");
    }

    public String getUserProfile(String username) {
        return JSONObject.fromObject(((User) UserDB.getInstance().getUser(username)).getProfile()).toString();
    }

    public String getUserRoles(String username) {
        User user = UserDB.getInstance().getUser(username);
        if (user != null) {
            return JSONArray.fromObject(user.getRoles()).toString();
        }
        return null;
    }

    public String getGroups() {
        return JSONArray.fromObject(UserDB.getInstance().getGroups()).toString();
    }

    public void alterUserGroup(String username, String groups) {
        if (username.equals("admin")) {
            if (groups.contains("\"admin\"")) {
                UserDB.getInstance().alterUserGroup(username, groups);
            }
        } else {
            UserDB.getInstance().alterUserGroup(username, groups);
        }
    }

    public String deleteUser(String username) {
        if (username.equals("admin")) {
            return "false";
        }
        fs.deleteAllByUser(username);
        return UserDB.getInstance().delete(username);
    }

    public String addGroup(String groupname) {
        if (groupname.equals("admin")) {
            return "false";
        }
        return UserDB.getInstance().addGroup(groupname);
    }

    public String deleteGroup(String groupname) {
        if (groupname.equals("admin")) {
            return "false";
        }
        return UserDB.getInstance().deleteGroup(groupname);
    }

    public String addGroupPermission(String groupname, String appid, String per) {
        if (groupname.equals("admin")) {
            return "false";
        }
        return UserDB.getInstance().addGroupPermission(groupname, appid, per);
    }

    public String updateGroupPermission(String groupname, String appid, String per) {
        if (groupname.equals("admin")) {
            return "false";
        }
        return UserDB.getInstance().updateGroupPermission(groupname, appid, per);
    }

    public String deleteGroupAppMap(String groupname, String appid) {
        if (groupname.equals("admin")) {
            return "false";
        }
        return UserDB.getInstance().deleteGroupAppMap(groupname, appid);
    }

    public String deleteGroupAppMapByGroup(String groupname) {
        if (groupname.equals("admin")) {
            return "false";
        }
        return UserDB.getInstance().deleteGroupAppMapByGroup(groupname);
    }

    public String existsGroup(String groupname) {
        if (UserDB.getInstance().existsGroup(groupname)) {
            return "true";
        }
        return "false";
    }

    public String existsUser(String username) {
        if (UserDB.getInstance().existsUser(username)) {
            return "true";
        }
        return "false";
    }

}
