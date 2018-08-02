package com.example.gilvi.oym;

import java.util.List;

// this interface is used for the async task to get the api data
interface ResponseListener {

    public void onFinished(List<Video> result);
}
