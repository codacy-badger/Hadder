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
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bbn.hadder.commands.misc;

import com.bbn.hadder.commands.Command;
import com.bbn.hadder.commands.CommandEvent;
import com.bbn.hadder.utils.MessageEditor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

public class CoronaCommand implements Command {

    @Override
    public void executed(String[] args, CommandEvent e) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://corona.lmao.ninja/v2/all").build();

        try {
            Response response = client.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder
                    .append("`Confirmed cases:` **").append(json.get("cases")).append("**\n")
                    .append("`Deaths:` **").append(json.get("deaths")).append("** \n")
                    .append("`Recovered:` **").append(json.get("recovered")).append("** \n")
                    .append("`Active cases:` **").append(json.get("active")).append("**");
            e.getTextChannel().sendMessage(e.getMessageEditor()
                    .getMessage(MessageEditor.MessageType.INFO)
                    .setDescription(stringBuilder)
                    .build()).queue();
        } catch (IOException ex) {
            e.getTextChannel().sendMessage(e.getMessageEditor().getMessage(MessageEditor.MessageType.ERROR)
                    .setTitle("API Error")
                    .setDescription("Try again later!")
                    .build()).queue();
        }
    }

    @Override
    public String[] labels() {
        return new String[]{"corona"};
    }

    @Override
    public String description() {
        return "commands.misc.corona.help.description";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String example() {
        return null;
    }
}
