package photon.bootstrap;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import photon.auth.OafService;
import photon.model.CrudService;
import photon.mybatis.MybatisCrudService;
import photon.mybatis.MybatisOafService;
import photon.query.QueryService;
import photon.query.QueryServiceImpl;

/**
 * Created by dan on 27/09/2017.
 */
public class PhotonModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(CrudService.class).to(MybatisCrudService.class);
        binder.bind(OafService.class).to(MybatisOafService.class);
        binder.bind(QueryService.class).to(QueryServiceImpl.class).in(Singleton.class);
    }
}
