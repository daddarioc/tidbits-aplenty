import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Tidbit from './tidbit';
import TidbitDetail from './tidbit-detail';
import TidbitUpdate from './tidbit-update';
import TidbitDeleteDialog from './tidbit-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TidbitUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TidbitUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TidbitDetail} />
      <ErrorBoundaryRoute path={match.url} component={Tidbit} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={TidbitDeleteDialog} />
  </>
);

export default Routes;
