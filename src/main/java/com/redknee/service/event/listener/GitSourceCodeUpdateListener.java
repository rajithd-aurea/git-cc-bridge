package com.redknee.service.event.listener;

import com.redknee.config.ApplicationProperty;
import com.redknee.config.ApplicationProperty.GitServer;
import com.redknee.service.event.SourceCodeEvent;
import com.redknee.service.exception.ApiException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class GitSourceCodeUpdateListener {

    private final ApplicationProperty applicationProperty;

    GitSourceCodeUpdateListener(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;
    }

    @EventListener
    public void handle(SourceCodeEvent sourceCode) {
        String dirPath = buildDirPath(sourceCode);
        GitServer gitServer = applicationProperty.getGitServer();
        CredentialsProvider credentials = gitServer.getUsername() != null && gitServer.getPassword() != null
                ? new UsernamePasswordCredentialsProvider(gitServer.getUsername(), gitServer.getPassword()) : null;
        try {
            if (isDirectoryExists(dirPath)) {
                Git git = Git.open(new File(dirPath));
                PullCommand pullCommand = git.pull();
                if (credentials != null) {
                    pullCommand.setCredentialsProvider(credentials);
                }
                PullResult pullResult = pullCommand.call();
                Collection<TrackingRefUpdate> updates = pullResult.getFetchResult().getTrackingRefUpdates();
                git.close();
            } else {
                CloneCommand cloneCommand = Git.cloneRepository()
                        .setURI(sourceCode.getUrl()).setDirectory(new File(dirPath));
                if (credentials != null) {
                    cloneCommand.setCredentialsProvider(credentials);
                }
                Git git = cloneCommand.call();
                git.close();
            }
        } catch (GitAPIException | IOException e) {
            throw new ApiException();
        }
    }

    private boolean isDirectoryExists(String dirName) {
        File src = new File(dirName);
        if (!src.isDirectory()) {
            return false;
        }
        String[] list = src.list();
        return list != null && list.length != 0;
    }

    private String buildDirPath(SourceCodeEvent sourceCode) {
        String workspace = applicationProperty.getGitServer().getWorkspace();
        return workspace + sourceCode.getRepoName() + "-" + sourceCode.getId();
    }

}
