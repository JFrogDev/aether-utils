package org.jfrog.aether.listeners

import org.codehaus.plexus.logging.Logger
import org.gcontracts.annotations.Requires
import org.sonatype.aether.AbstractRepositoryListener
import org.sonatype.aether.RepositoryEvent
import org.sonatype.aether.repository.ArtifactRepository
import org.sonatype.aether.repository.Authentication
import org.sonatype.aether.repository.RemoteRepository


class SonatypeRepositoryListener extends AbstractRepositoryListener {

    private final String  repoUrl
    private final String  repoUsername
    private final String  repoPassword
    private final String  proxyHost
    private final Integer proxyPort
    private final String  proxyUsername
    private final String  proxyPassword
    private final Logger  logger

    @SuppressWarnings([ 'GroovyMethodParameterCount' ])
    SonatypeRepositoryListener ( String  repoUrl,
                                 String  repoUsername,
                                 String  repoPassword,
                                 String  proxyHost,
                                 Integer proxyPort,
                                 String  proxyUsername,
                                 String  proxyPassword,
                                 Logger  logger )
    {
        this.repoUrl       = repoUrl
        this.repoUsername  = repoUsername
        this.repoPassword  = repoPassword
        this.proxyHost     = proxyHost
        this.proxyPort     = proxyPort
        this.proxyUsername = proxyUsername
        this.proxyPassword = proxyPassword
        this.logger        = logger
    }

    @Override
    @Requires({ event })
    public void artifactDownloading(RepositoryEvent event) {
        logger.debug("Intercepted artifact downloading event: " + event)
        enforceRepository(event)
        super.artifactDownloading(event)
    }

    @Override
    @Requires({ event })
    public void metadataDownloading(RepositoryEvent event) {
        logger.debug("Intercepted metadata downloading event: " + event)
        enforceRepository(event)
        super.metadataDownloading(event)
    }

    @SuppressWarnings([ 'GroovyAccessibility' ])
    private void enforceRepository(RepositoryEvent event) {
        ArtifactRepository repository = event.repository

        if ( repository instanceof RemoteRepository ){

            final remoteRepository = ( RemoteRepository ) repository

            logger.debug( "Enforcing repository URL: $repoUrl for event: $event" )

            if ( ! repoUrl ) { return }

            remoteRepository.url = repoUrl

            if ( repoUsername ) {
                Authentication authentication   = new Authentication( repoUsername, repoPassword )
                logger.debug( "Enforcing repository authentication: $authentication for event: $event" )
                remoteRepository.authentication = authentication
            }

            if ( proxyHost ) {
                remoteRepository.proxy =
                    new org.sonatype.aether.repository.Proxy( null, proxyHost, proxyPort, new Authentication( proxyUsername, proxyPassword ))
            }
        }
    }
}
