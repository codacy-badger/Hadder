/*
 * Copyright 2019-2020 GregTCLTK and Schlauer-Hax
 *
 * Licensed under the GNU Affero General Public License, Version 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.gnu.org/licenses/agpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bbn.hadder.db;

import com.bbn.hadder.core.Config;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlNonExistenceError;
import com.rethinkdb.gen.exc.ReqlOpFailedError;
import com.rethinkdb.net.Connection;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class Rethink {
    private final RethinkDB r = RethinkDB.r;
    private Connection conn;
    private final Config config;

    public Rethink(Config config) {
        this.config = config;
    }

    public void connect() {
        try {
            conn = r.connection()
                    .hostname(config.getDatabaseIP())
                    .db(config.getDatabaseName())
                    .port(config.getDatabasePort())
                    .user(config.getDatabaseUsername(), config.getDatabasePassword())
                    .connect();
            System.out.println("DB CONNECTED");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DB CONNECTION FAILED");
        }
    }

    public Object getByID(String table, String where, String column) {
        return r.table(table).get(where).getField(column).run(conn).first();
    }

    public JSONObject getObjectByID(String table, String id) {
        String response = String.valueOf(r.table(table).get(id).toJson().run(conn).first());
        try {
            return new JSONObject(response);
        } catch (JSONException e) {
            insertUser(id);
            String response2 = String.valueOf(r.table(table).get(id).toJson().run(conn).first());
            try {
                return new JSONObject(response2);
            } catch (JSONException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    public void insert(String table, Object object) {
        try {
            r.table(table).insert(object).run(conn);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public void remove(String table, String where, String value) {
        r.table(table).filter(row -> row.g(where.toLowerCase()).eq(value)).delete().run(conn);
    }

    public void setup() {
        try {
            r.dbCreate("Hadder").run(conn);
        } catch (ReqlOpFailedError e) {
            System.out.println(e.getMessage());
        }
        try {
            r.tableCreate("server").run(conn);
        } catch (ReqlOpFailedError e) {
            System.out.println(e.getMessage());
        }
        try {
            r.tableCreate("user").run(conn);
        } catch (ReqlOpFailedError e) {
            System.out.println(e.getMessage());
        }
        try {
            r.tableCreate("stars").run(conn);
        } catch (ReqlOpFailedError e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertGuild(String id) {
        this.insert("server", r
                .hashMap("id", id)
                .with("prefix", "h.")
                .with("message_id", "")
                .with("role_id", "")
                .with("invite_detect", false)
                .with("starboard", "")
                .with("neededstars", "4")
        );
    }

    public void insertUser(String id) {
        this.insert("user", r
                .hashMap("id", id)
                .with("prefix", "h.")
                .with("language", "en")
                .with("blacklisted", "none"));
    }

    public void insertStarboardMessage(String message_id, String guild_id, String starboard_message_id) {
        this.insert("stars", r.hashMap("id", message_id).with("guild", guild_id).with("starboardmsg", starboard_message_id));
    }

    public String getStarboardMessage(String message_id) {
        return (String) this.getByID("stars", message_id, "starboardmsg");
    }

    public void removeStarboardMessage(String message_id) {
        this.remove("stars", "id", message_id);
    }

    public boolean hasStarboardMessage(String message_id) {
        try {
            this.getByID("stars", message_id, "guild");
            return true;
        } catch (ReqlNonExistenceError e) {
            return false;
        }
    }

    public void push(RethinkServer server) {
        JSONObject object = new JSONObject();
        for (Field field : server.getClass().getDeclaredFields()) {
            if (!field.getName().equals("rethink")) {
                try {
                    object.put(field.getName(), field.get(server));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        r.table("server").get(server.getId()).update(object.toMap()).run(conn);
    }

    public void push(RethinkUser user) {
        JSONObject object = new JSONObject();
        for (Field field : user.getClass().getDeclaredFields()) {
            if (!field.getName().equals("rethink")) {
                try {
                    object.put(field.getName(), field.get(user));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        r.table("user").get(user.getId()).update(object.toMap()).run(conn);
    }
}
