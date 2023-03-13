/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;


    private static BoardOverviewCtrl boardOverviewCtrl;
    private Scene boardOverview;
    private Scene add;

    private Scene createList;
    private CreateListCtrl createListCtrl;

    private SelectServerCtrl selectServerCtrl;

    private Scene selectServer;

    public void initialize(final Stage primaryStage, final Pair<QuoteOverviewCtrl, Parent> overview,
                           final Pair<AddQuoteCtrl, Parent> add,
                           final Pair<BoardOverviewCtrl, Parent> boardOverview,
                           final Pair<CreateListCtrl, Parent> createList,
                           final Pair<SelectServerCtrl, Parent> selectServer) {
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.boardOverviewCtrl = boardOverview.getKey();
        this.boardOverview = new Scene(boardOverview.getValue());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        this.createListCtrl = createList.getKey();
        this.createList = new Scene(createList.getValue());

        this.selectServerCtrl=selectServer.getKey();
        this.selectServer= new Scene(selectServer.getValue());

        //showBoardOverview();
        //showOverview();
        showSelectServer();
        primaryStage.show();
    }

    public void showOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
    }

    public void showBoardOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(boardOverview);
        overviewCtrl.refresh();
    }

    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    public void showSelectServer() {
        primaryStage.setTitle("Talio: select your server");
        primaryStage.setScene(selectServer);
    }

    /**
     * Changes the scene to the popup that allows users to create a new task list and name it.
     */
    public void showCreateList() {
        primaryStage.setTitle("Talio: Create List");
        primaryStage.setScene(createList);
        overviewCtrl.refresh();
    }

    /**
     *
     * @return the instance of boardOverviewControl in order to be able to use the addList method.
     * Needs fixing. addList method should be done on the server side so that the lists can be stored.
     */
    public static BoardOverviewCtrl getBoardOverviewCtrl() {
        return boardOverviewCtrl;
    }
}