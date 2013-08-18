package org.jfrog.aether

import org.apache.maven.execution.MavenSession
import org.codehaus.plexus.logging.Logger
import org.gcontracts.annotations.Requires
import org.jfrog.aether.listeners.EclipseRepositoryListener
import org.jfrog.aether.listeners.SonatypeRepositoryListener


class AetherUtils
{
    @SuppressWarnings([ 'GroovyAccessibility' ])
    @Requires({ session && defaultRepoSessionClass })
    static void addRepositoryListener( MavenSession session,
                                       String  defaultRepoSessionClass,
                                       String  repoUrl,
                                       String  repoUsername,
                                       String  repoPassword,
                                       String  proxyHost,
                                       Integer proxyPort,
                                       String  proxyUsername,
                                       String  proxyPassword,
                                       Logger  logger)
    {
        final repoSessionClass = session.repositorySession.class.name

        if ( ! repoSessionClass.endsWith( defaultRepoSessionClass )) { return }

        if ( repoSessionClass.startsWith( 'org.eclipse' ))
        {
            session.repositorySession.readOnly = false
            session.repositorySession.repositoryListener =
                new org.eclipse.aether.util.listener.ChainedRepositoryListener( session.repositorySession.repositoryListener,
                    new EclipseRepositoryListener( repoUrl, repoUsername, repoPassword, proxyHost, proxyPort, proxyUsername, proxyPassword, logger ))
        }
        else
        {
            session.repositorySession.repositoryListener =
                new org.sonatype.aether.util.listener.ChainedRepositoryListener( session.repositorySession.repositoryListener,
                    new SonatypeRepositoryListener( repoUrl, repoUsername, repoPassword, proxyHost, proxyPort, proxyUsername, proxyPassword, logger ))
        }
    }
}
