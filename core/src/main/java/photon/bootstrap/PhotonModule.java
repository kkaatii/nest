package photon.bootstrap;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import photon.crud.OafService;
import photon.crud.CrudService;
import photon.crud.mybatis.MybatisCrudService;
import photon.crud.mybatis.MybatisOafService;
import photon.query.QueryService;
import photon.query.QueryServiceImpl;

public class PhotonModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(CrudService.class).to(MybatisCrudService.class);
        binder.bind(OafService.class).to(MybatisOafService.class);
        binder.bind(QueryService.class).to(QueryServiceImpl.class).in(Singleton.class);
    }
}
