import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './tidbit.reducer';
import { ITidbit } from 'app/shared/model/tidbit.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ITidbitDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class TidbitDetail extends React.Component<ITidbitDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { tidbitEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="tidbitsApp.tidbit.detail.title">Tidbit</Translate> [<b>{tidbitEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="content">
                <Translate contentKey="tidbitsApp.tidbit.content">Content</Translate>
              </span>
            </dt>
            <dd>{tidbitEntity.content}</dd>
            <dt>
              <span id="author">
                <Translate contentKey="tidbitsApp.tidbit.author">Author</Translate>
              </span>
            </dt>
            <dd>{tidbitEntity.author}</dd>
            <dt>
              <span id="source">
                <Translate contentKey="tidbitsApp.tidbit.source">Source</Translate>
              </span>
            </dt>
            <dd>{tidbitEntity.source}</dd>
            <dt>
              <span id="url">
                <Translate contentKey="tidbitsApp.tidbit.url">Url</Translate>
              </span>
            </dt>
            <dd>{tidbitEntity.url}</dd>
            <dt>
              <Translate contentKey="tidbitsApp.tidbit.category">Category</Translate>
            </dt>
            <dd>{tidbitEntity.categoryName ? tidbitEntity.categoryName : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/tidbit" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/tidbit/${tidbitEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.edit">Edit</Translate>
            </span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ tidbit }: IRootState) => ({
  tidbitEntity: tidbit.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(TidbitDetail);
