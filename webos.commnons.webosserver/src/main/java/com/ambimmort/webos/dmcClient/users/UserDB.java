package com.ambimmort.webos.dmcClient.users;

import com.ambimmort.webos.commons.cassandradb.core.CassandraSimpleDBBridge;
import com.ambimmort.webos.commons.cassandradb.core.DBBridge;
import com.datastax.driver.core.Row;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

public class UserDB {

    private Logger log = Logger.getLogger(UserDB.class);
    private static UserDB instance = null;

    private DBBridge bridge = null;

    private UserDB() {
        init();
    }

    public static synchronized UserDB getInstance() {
        if (instance == null) {
            instance = new UserDB();
        }
        return instance;
    }

    public User getUser(String username) {
        String cql = "SELECT * FROM user WHERE username='" + username + "';";
        Iterator rows = this.bridge.excute(cql);
        User user = null;
        while (rows.hasNext()) {
            Row row = (Row) rows.next();
            user = new User();
            user.setUsername(row.getString("username"));
            user.setProfile(row.getString("profile"));
            user.setRoles(row.getString("roles"));
        }
        return user;
    }

    public List<User> getUsers() {
        String cql = "SELECT * FROM user;";
        Iterator rows = this.bridge.excute(cql);
        List<User> users = new ArrayList<User>();

        while (rows.hasNext()) {
            Row row = (Row) rows.next();
            User user = new User();
            user.setUsername(row.getString("username"));
            user.setProfile(row.getString("profile"));
            user.setRoles(row.getString("roles"));
            users.add(user);
        }
        return users;
    }

    private void init() {
        this.bridge = new CassandraSimpleDBBridge();
    }

    public Set<String> getGroups() {
        String cql = "SELECT * FROM group;";
        Iterator rows = this.bridge.excute(cql);
        Set<String> groups = new HashSet<String>();

        while (rows.hasNext()) {
            Row row = (Row) rows.next();
            groups.add(row.getString("groupname"));
        }
        return groups;
    }

    public void alterUserGroup(String username, String groups) {
        String cql = "UPDATE user SET roles='%s' WHERE username='%s'";
        cql = String.format(cql, groups, username);
        this.bridge.excute(cql);
        String cql2 = "DELETE FROM user_group_map WHERE username='" + username + "';";
        this.bridge.excute(cql2);
        JSONArray arr = JSONArray.fromObject(groups);
        Object[] gs = arr.toArray();
        for (Object g : gs) {
            cql2 = "INSERT INTO user_group_map(username,groupname) VALUES('%s','%s');";
            cql2 = String.format(cql2, username, g.toString());
            this.bridge.excute(cql2);
        }
    }

    public void deleteGroup2(String groupname) {

    }

    public String addUser(String name, String profile, String roles) {
        String cql = "INSERT INTO user(username,profile,roles) VALUES('%s','%s','%s');";
        cql = String.format(cql, name, profile, roles);
        String result = "{\"rst\":true}";
        try {
            this.bridge.excute(cql);
        } catch (Exception e) {
            e.printStackTrace();
            result = "{\"rst\":false}";
        }
        return result;
    }

    public String delete(String username) {
        String cql = "DELETE FROM user WHERE username='" + username + "';";
        String result = "{\"rst\":true}";
        try {
            this.bridge.excute(cql);
        } catch (Exception e) {
            result = "{\"rst\":false}";
        }

        return result;
    }

    public String addGroup(String groupname) {
        String cql = "INSERT INTO group(groupname) VALUES('%s');";
        cql = String.format(cql, groupname);
        String result = "true";
        try {
            this.bridge.excute(cql);
        } catch (Exception e) {
            result = "false";
        }
        return result;
    }

    public boolean existsGroup(String groupname) {
        String cql = "SELECT * FROM group WHERE groupname='%s';";
        cql = String.format(cql, groupname);
        boolean result = false;
        try {
            return this.bridge.excute(cql).hasNext();
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public boolean existsUser(String username) {
        String cql = " SELECT * FROM user WHERE username='%s';";
        cql = String.format(cql, username);
        boolean result = false;
        try {
            return this.bridge.excute(cql).hasNext();
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public String deleteGroup(String groupname) {
        String cql = "DELETE FROM group WHERE groupname='" + groupname + "';";
        String result = "true";
        try {
            this.bridge.excute(cql);
            deleteGroupAppMapByGroup(groupname);
            cql = "SELECT * FROM user_group_map WHERE groupname='%s' ALLOW FILTERING;";
            cql = String.format(cql, groupname);
            Iterator rows = this.bridge.excute(cql);

            while (rows.hasNext()) {
                Row row = (Row) rows.next();
                String username = row.getString("username");
                JSONArray roles = JSONArray.fromObject(this.getUser(username).getRoles());
                roles.remove(groupname);
                this.alterUserGroup(username, roles.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result = "false";
        }

        return result;
    }

    public String addGroupPermission(String groupname, String appid, String per) {
        String cql = "INSERT INTO group_app_map(groupname,appid,permissions) VALUES('%s','%s','%s');";
        cql = String.format(cql, groupname, appid, per);
        String result = "true";
        try {
            this.bridge.excute(cql);
        } catch (Exception e) {
            result = "false";
        }
        return result;
    }

    public String updateGroupPermission(String groupname, String appid, String per) {
        String cql = "UPDATE group_app_map SET permissions = '%s' WHERE groupname='%s' AND appid='%s';";
        cql = String.format(cql, per, groupname, appid);
        String result = "true";
        try {
            //deleteGroupAppMapByGroup(groupname);
            this.bridge.excute(cql);
        } catch (Exception e) {
            result = "false";
        }
        return result;
    }

    public String deleteGroupAppMapByGroup(String groupname) {
        String cql = "DELETE FROM group_app_map WHERE groupname='%s';";
        cql = String.format(cql, groupname);
        String result = "true";
        try {
            this.bridge.excute(cql);
        } catch (Exception e) {
            result = "false";
        }
        return result;
    }

    public String deleteGroupAppMap(String groupname, String appid) {
        String cql = "DELETE FROM group_app_map WHERE groupname='%s' AND appid ='%s';";
        cql = String.format(cql, groupname, appid);
        String result = "true";
        try {
            this.bridge.excute(cql);
        } catch (Exception e) {
            result = "false";
        }
        return result;
    }

    public void shutdown() {
        this.bridge.close();
    }

}
