package org.reactor.tfs;

import org.apache.http.impl.auth.NTLMEngineException;
import org.reactor.AbstractNestingReactor;
import org.reactor.ReactorInitializationException;
import org.reactor.ReactorProperties;
import org.reactor.annotation.ReactOn;
import org.reactor.request.ReactorRequest;
import org.reactor.response.ReactorResponse;
import org.reactor.response.list.ListElementFormatter;
import org.reactor.response.list.ListReactorResponse;
import org.reactor.tfs.client.TFSClient;
import org.reactor.tfs.client.model.AntiForgeryData;
import org.reactor.tfs.client.model.WorkItem;
import org.reactor.tfs.client.model.WorkItemColumn;
import org.reactor.tfs.client.service.WorkItemsService;
import org.reactor.tfs.request.BugsListRequest;

import java.io.IOException;
import java.util.Collection;

import static com.google.common.collect.FluentIterable.from;
import static java.lang.String.format;
import static org.reactor.tfs.client.TFSClientBuilder.clientBuilder;
import static org.reactor.tfs.client.model.WorkItemColumn.ASSIGNED_TO;
import static org.reactor.tfs.client.model.WorkItemColumn.REMAINING_WORK;
import static org.reactor.tfs.client.model.WorkItemColumn.TITLE;
import static org.reactor.tfs.client.model.WorkItemColumn.TYPE;
import static org.reactor.tfs.client.model.WorkItemState.CLOSED;
import static org.reactor.tfs.client.model.WorkItemState.RESOLVED;
import static org.reactor.tfs.client.model.WorkItemType.BUG;
import static org.reactor.tfs.client.query.WorkItemQueryBuilder.query;
import static org.reactor.tfs.client.query.WorkItemQueryWhereBuilder.systemState;
import static org.reactor.tfs.client.query.WorkItemQueryWhereBuilder.teamProject;
import static org.reactor.tfs.client.query.WorkItemQueryWhereBuilder.type;

/**
 * @author grabslu
 */
@ReactOn(value = "tfs", description = "Provides an access to Microsoft TFS server")
public class TfsReactor extends AbstractNestingReactor {

    private TFSClient tfsClient;
    private AntiForgeryData antiForgeryData;
    private String projectName;

    @Override
    protected void initNestingReactor(ReactorProperties properties) {
        try {
            initTfsReactor(new TfsReactorProperties(properties));
        } catch (NTLMEngineException | IOException e) {
            throw new ReactorInitializationException(e);
        }
    }

    @ReactOn(value = "bugs", description = "Prints out list of bugs")
    public ReactorResponse bugs(ReactorRequest<BugsListRequest> bugsListRequest) throws IOException {
        WorkItemsService workItemsService = tfsClient.getWorkItemsService();
        Collection<WorkItem> workItems = workItemsService.queryWorkItems(projectName,
                query().select(TITLE, ASSIGNED_TO, REMAINING_WORK, TYPE)
                        .where(teamProject(projectName)
                                .and(systemState(RESOLVED).not().and(systemState(CLOSED).not())).and(type(BUG)))
                        .build(), antiForgeryData.getRequestVerificationToken());
        if (bugsListRequest.getRequestData().isCountOnly()) {
            return responseRenderer -> responseRenderer.renderLongLine("count", from(workItems).size());
        }
        return new ListReactorResponse<WorkItem>() {
            @Override
            protected Iterable<WorkItem> getElements() {
                return workItems;
            }

            @Override
            protected ListElementFormatter<WorkItem> getElementFormatter() {
                return (elementIndex, listElement) -> {
                    String title = listElement.getColumnValue(TITLE).get().getString().get();
                    String id = listElement.getColumnValue(WorkItemColumn.ID).get().getString().get();

                    return format("%s -> %s", id, title);
                };
            }
        };
    }

    private void initTfsReactor(TfsReactorProperties tfsReactorProperties) throws NTLMEngineException, IOException {
        tfsClient = clientBuilder()
                .serviceUrl(tfsReactorProperties.getServiceUrl())
                .username(tfsReactorProperties.getUsername())
                .password(tfsReactorProperties.getPassword())
                .domain(tfsReactorProperties.getDomain())
                .build();
        antiForgeryData = tfsClient.getAntiForgeryCrackerService().getAntiForgeryData(projectName = tfsReactorProperties.getProject());
    }
}
