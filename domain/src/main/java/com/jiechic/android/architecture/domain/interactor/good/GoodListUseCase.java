package com.jiechic.android.architecture.domain.interactor.good;

import com.jiechic.android.architecture.domain.entity.response.GoodList;
import com.jiechic.android.architecture.domain.executor.PostExecutionThread;
import com.jiechic.android.architecture.domain.executor.ThreadExecutor;
import com.jiechic.android.architecture.domain.interactor.UseCase;
import com.jiechic.android.architecture.domain.repository.DataRepository;
import rx.Observable;

import javax.inject.Inject;

/**
 * Created by jiechic on 15/10/30.
 */
public class GoodListUseCase extends UseCase {
    private final DataRepository dataRepository;

    private int page;

    //使用注入，自动生成该UseCase
    @Inject
    protected GoodListUseCase(DataRepository dataRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.dataRepository=dataRepository;
    }

    public void setParam(int page){
        this.page=page;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return dataRepository.goodList(this.page);
    }

    @Override
    public Observable<GoodList> observable() {
        return execute();
    }
}
