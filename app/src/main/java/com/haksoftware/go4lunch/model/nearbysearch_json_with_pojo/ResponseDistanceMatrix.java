package com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo;

import java.util.List;

public class ResponseDistanceMatrix{
	private List<String> destinationAddresses;
	private List<RowsItem> rows;
	private List<String> originAddresses;
	private String status;

	public List<String> getDestinationAddresses(){
		return destinationAddresses;
	}

	public List<RowsItem> getRows(){
		return rows;
	}

	public List<String> getOriginAddresses(){
		return originAddresses;
	}

	public String getStatus(){
		return status;
	}
}