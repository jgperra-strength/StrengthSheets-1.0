package com.example.powersheet.Spreadsheet;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    //Training sheet metadata

}

//write metadata
            /*List<Request> requests = new ArrayList<>();
            requests.add(new Request().setCreateDeveloperMetadata(new CreateDeveloperMetadataRequest()
                    .setDeveloperMetadata(new DeveloperMetadata().setMetadataKey("numBlocks")
                            .setLocation(new DeveloperMetadataLocation().setSpreadsheet(true)).setMetadataValue("0").setVisibility("PROJECT"))));

            BatchUpdateSpreadsheetResponse r2 =
                    this.mService.spreadsheets().batchUpdate(spreadsheetId, new BatchUpdateSpreadsheetRequest().setRequests(requests))
                            .execute();*/

//read metadata
            /*ArrayList<DataFilter> filters = new ArrayList<>();
            filters.add(new DataFilter().setDeveloperMetadataLookup(new DeveloperMetadataLookup()
                            .setMetadataKey("numBlocks")));

            SearchDeveloperMetadataResponse r3 = this.mService.spreadsheets().developerMetadata().search(spreadsheetId, new SearchDeveloperMetadataRequest()
                    .setDataFilters(filters)).execute();

            System.out.println("METADATA = " + r3);*/

//delete metadata
            /*List<Request> requests = new ArrayList<>();
            requests.add(new Request().setDeleteDeveloperMetadata(new DeleteDeveloperMetadataRequest()
                    .setDataFilter(new DataFilter().setDeveloperMetadataLookup(new DeveloperMetadataLookup().setMetadataKey("numBlocks")))));

            BatchUpdateSpreadsheetResponse r2 =
                    this.mService.spreadsheets().batchUpdate(spreadsheetId, new BatchUpdateSpreadsheetRequest().setRequests(requests))
                            .execute();

            System.out.println("METADATA = " + r2);*/