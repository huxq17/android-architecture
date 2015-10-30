package com.jiechic.android.architecture.domain.interactor.good;

import com.jiechic.android.architecture.domain.entity.data.Good;
import com.jiechic.android.architecture.domain.executor.PostExecutionThread;
import com.jiechic.android.architecture.domain.executor.ThreadExecutor;
import com.jiechic.android.architecture.domain.interactor.UseCase;
import com.jiechic.android.architecture.domain.repository.DataRepository;
import rx.Observable;

import javax.inject.Inject;

/**
 * Created by jiechic on 15/10/30.
 */
public class GoodCreateUseCase extends UseCase{
    private final DataRepository dataRepository;

    private Good good;

    //使用注入，自动生成该UseCase
    @Inject
    protected GoodCreateUseCase(DataRepository dataRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.dataRepository=dataRepository;
    }

    public void setParam(Good good){
        this.good=good;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return dataRepository.goodCreate(this.good);
    }

    @Override
    public Observable<Good> observable() {
        return execute();
    }
}
