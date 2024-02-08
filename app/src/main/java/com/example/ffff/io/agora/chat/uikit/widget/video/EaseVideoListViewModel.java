package io.agora.chat.uikit.widget.video;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.agora.chat.uikit.models.EaseVideoEntity;

public class EaseVideoListViewModel extends AndroidViewModel {
    private io.agora.chat.uikit.widget.video.EaseSingleSourceLiveData<List<EaseVideoEntity>> videoListObservable;
    private io.agora.chat.uikit.widget.video.EaseMediaManagerRepository repository;

    public EaseVideoListViewModel(@NonNull Application application) {
        super(application);
        repository = new io.agora.chat.uikit.widget.video.EaseMediaManagerRepository();
        videoListObservable = new io.agora.chat.uikit.widget.video.EaseSingleSourceLiveData<>();
    }

    public LiveData<List<EaseVideoEntity>> getVideoListObservable() {
        return videoListObservable;
    }

    public void getVideoList(Context context) {
        videoListObservable.setSource(repository.getVideoListFromMediaAndSelfFolder(context));
    }

}

