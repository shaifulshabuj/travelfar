# CI/CD Pipeline Documentation

## Overview

This project uses **GitHub Actions** for continuous integration and continuous deployment (CI/CD). The pipeline is designed following engineering standards with comprehensive testing, security scanning, and automated deployments.

## Pipeline Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Push to main/develop                     │
└─────────────────────┬───────────────────────────────────────┘
                      │
        ┌─────────────┴─────────────┐
        │                           │
        ▼                           ▼
┌───────────────┐          ┌────────────────┐
│ Build & Test  │          │ Code Quality   │
└───────┬───────┘          └────────┬───────┘
        │                           │
        ├───────────┬───────────────┤
        │           │               │
        ▼           ▼               ▼
┌───────────┐ ┌──────────┐ ┌──────────────┐
│  Docker   │ │ Security │ │ Notification │
│   Build   │ │   Scan   │ │              │
└─────┬─────┘ └──────────┘ └──────────────┘
      │
      ▼
┌─────────────┐
│   Deploy    │
│   Staging   │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Deploy    │
│ Production  │
│ (Manual)    │
└─────────────┘
```

## Workflows

### 1. Main CI/CD Pipeline (`ci-cd.yml`)

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches

**Jobs:**

#### a) Build and Test
- Sets up Java 17 and Maven
- Starts MySQL and Redis services
- Runs unit tests
- Runs integration tests
- Generates code coverage reports
- Uploads build artifacts

**Services:**
- MySQL 8.0 (port 3306)
- Redis 7 (port 6379)

#### b) Code Quality Analysis
- Runs Maven Checkstyle
- Runs SpotBugs analysis
- Ensures code quality standards

#### c) Docker Build
- Builds Docker image
- Pushes to Docker Hub (if configured)
- Tags with branch name, SHA, and latest

#### d) Security Scan
- OWASP dependency vulnerability check
- Generates security reports

#### e) Deploy to Staging
- Deploys to staging environment
- Runs smoke tests
- Only on `main` branch

#### f) Deploy to Production
- Requires manual approval (GitHub Environments)
- Deploys to production
- Runs health checks
- Only on `main` branch

### 2. Pull Request Checks (`pr-checks.yml`)

**Triggers:**
- Pull request opened, synchronized, or reopened

**Jobs:**
- Validates Maven POM
- Checks code formatting
- Runs quick tests
- Validates PR title (semantic versioning)
- Comments on PR with results

### 3. Docker Publish (`docker-publish.yml`)

**Triggers:**
- Push to `main` branch
- Git tags starting with `v*`
- Release published

**Jobs:**
- Builds multi-platform Docker images (amd64, arm64)
- Pushes to GitHub Container Registry (ghcr.io)
- Generates SBOM (Software Bill of Materials)
- Scans for vulnerabilities with Trivy
- Uploads security results to GitHub Security

### 4. Dependency Updates (`dependency-update.yml`)

**Triggers:**
- Scheduled: Every Monday at 9 AM UTC
- Manual trigger via workflow dispatch

**Jobs:**
- Checks for Maven dependency updates
- Creates pull request with updates
- Automated dependency maintenance

### 5. Dependabot (`dependabot.yml`)

**Configuration:**
- Weekly dependency updates for Maven, GitHub Actions, and Docker
- Automatically creates PRs
- Limits open PRs to prevent overwhelming maintainers

## Environment Setup

### Required Secrets

Add these secrets in GitHub repository settings (Settings → Secrets and variables → Actions):

| Secret Name | Description | Required |
|------------|-------------|----------|
| `DOCKER_USERNAME` | Docker Hub username | Optional |
| `DOCKER_PASSWORD` | Docker Hub password/token | Optional |
| `SLACK_WEBHOOK_URL` | Slack webhook for notifications | Optional |

### GitHub Environments

Create these environments in repository settings (Settings → Environments):

1. **staging**
   - URL: https://staging.travel-api.example.com
   - No approval required

2. **production**
   - URL: https://api.travel-api.example.com
   - Requires approval from maintainers
   - Protection rules: 1+ reviewer required

## Running Locally

### Test the Build Pipeline

```bash
# Run all tests
mvn clean test

# Run with coverage
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Test Docker Build

```bash
# Build image locally
docker build -t travel-reservation-api:local .

# Run with docker-compose
docker-compose up -d

# Check logs
docker logs travel-app
```

## Monitoring Pipeline Status

### Status Badges

Add to README.md:

```markdown
![CI/CD](https://github.com/shaifulshabuj/travelfar/workflows/CI/CD%20Pipeline/badge.svg)
![Docker](https://github.com/shaifulshabuj/travelfar/workflows/Docker%20Build%20and%20Publish/badge.svg)
```

### View Pipeline Runs

1. Go to GitHub repository
2. Click "Actions" tab
3. Select workflow from left sidebar
4. View recent runs and logs

## Deployment Process

### Automatic Deployment (Staging)

1. Push code to `main` branch
2. CI/CD pipeline runs automatically
3. If all tests pass, deploys to staging
4. Smoke tests run against staging
5. Notification sent

### Manual Deployment (Production)

1. Staging deployment completes successfully
2. Go to Actions → Deploy to Production workflow
3. Review changes and approve deployment
4. Production deployment proceeds
5. Health checks verify deployment
6. Notification sent

## Troubleshooting

### Pipeline Fails on Test

```bash
# Run tests locally with verbose output
mvn test -X

# Check specific test
mvn test -Dtest=HotelSearchServiceTest
```

### Docker Build Fails

```bash
# Check Dockerfile syntax
docker build --no-cache -t test .

# Check build context
du -sh .
```

### Coverage Below Threshold

```bash
# Generate coverage report
mvn jacoco:report

# View report
open target/site/jacoco/index.html

# Write more tests for uncovered code
```

## Best Practices

### Commit Messages

Follow conventional commits format:

```
feat: add new hotel search filter
fix: resolve caching issue in Redis
docs: update API documentation
test: add integration tests for reservations
ci: update GitHub Actions workflow
```

### Pull Requests

- Keep PRs small and focused
- Write descriptive PR titles
- Fill out PR template completely
- Ensure all CI checks pass
- Request review from team members

### Testing

- Write unit tests for all service layer logic
- Maintain minimum 60% code coverage
- Test error scenarios
- Use meaningful test names

### Security

- Never commit secrets or credentials
- Use GitHub secrets for sensitive data
- Review dependency updates for security issues
- Monitor security scan results

## Pipeline Metrics

Track these metrics for pipeline health:

- **Build Time**: Target < 5 minutes
- **Test Success Rate**: Target > 95%
- **Coverage**: Minimum 60%, target 80%
- **Deployment Frequency**: Daily to staging
- **MTTR** (Mean Time To Recovery): Target < 1 hour

## Continuous Improvement

### Planned Enhancements

- [ ] Add performance testing
- [ ] Implement canary deployments
- [ ] Add A/B testing capability
- [ ] Enhanced security scanning
- [ ] Automated rollback on failure
- [ ] Integration with monitoring tools (Prometheus, Grafana)

## Support

For issues with the CI/CD pipeline:

1. Check GitHub Actions logs
2. Review this documentation
3. Contact DevOps team
4. Create issue in repository

---

**Last Updated:** January 14, 2026  
**Maintained by:** Team
