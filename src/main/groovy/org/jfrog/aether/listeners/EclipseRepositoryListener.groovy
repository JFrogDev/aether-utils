package org.jfrog.aether.listeners

import org.codehaus.plexus.logging.Logger
import org.eclipse.aether.AbstractRepositoryListener
import org.eclipse.aether.RepositoryEvent
import org.eclipse.aether.repository.ArtifactRepository
import org.eclipse.aether.repository.Authentication
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.util.repository.SecretAuthentication
import org.gcontracts.annotations.Requires


class EclipseRepositoryListener extends AbstractRepositoryListener {

    private final String  repoUrl
    private final String  repoUsername
    private final String  repoPassword
    private final String  proxyHost
    private final Integer proxyPort
    private final String  proxyUsername
    private final String  proxyPassword
    private final Logger  logger


    @SuppressWarnings([ 'GroovyMethodParameterCount' ])
    EclipseRepositoryListener ( String  repoUrl,
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
                Authentication authentication   = new SecretAuthentication( repoUsername, repoPassword )
                logger.debug( "Enforcing repository authentication: $authentication for event: $event" )
                remoteRepository.authentication = authentication
            }

            if ( proxyHost ) {
                remoteRepository.proxy =
                    new org.eclipse.aether.repository.Proxy( null, proxyHost, proxyPort, new SecretAuthentication( proxyUsername, proxyPassword ))
            }
        }
    }
}
