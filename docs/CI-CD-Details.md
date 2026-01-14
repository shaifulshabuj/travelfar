# CI/CD

1. **`.github/workflows/ci-cd.yml`** - Main CI/CD Pipeline
   - Build and test with MySQL and Redis services
   - Code coverage with JaCoCo (60% minimum)
   - Docker image building
   - Security vulnerability scanning
   - Staging deployment
   - Production deployment (with manual approval)
   - Notification system

2. **`.github/workflows/pr-checks.yml`** - Pull Request Validation
   - Maven POM validation
   - Code formatting checks
   - Quick test execution
   - Semantic PR title validation

3. **`.github/workflows/docker-publish.yml`** - Docker Publishing
   - Multi-platform builds (amd64, arm64)
   - GitHub Container Registry integration
   - SBOM (Software Bill of Materials) generation
   - Trivy security scanning
   - GitHub Security integration

4. **`.github/workflows/dependency-update.yml`** - Dependency Management
   - Weekly dependency updates
   - Automated PR creation
   - Maven version checks

5. **`.github/dependabot.yml`** - Dependabot Configuration
   - Maven dependencies
   - GitHub Actions
   - Docker images
   - Weekly update schedule

### Additional Files

6. **`.github/PULL_REQUEST_TEMPLATE.md`** - PR Template
   - Standardized PR format
   - Checklist for reviewers
   - standards compliance

7. **`docs/CI-CD.md`** - Comprehensive CI/CD Documentation
   - Pipeline architecture
   - Workflow descriptions
   - Secrets configuration
   - Troubleshooting guide
   - Best practices

### Maven Enhancements

Updated `pom.xml` with:
- **JaCoCo** for code coverage (60% minimum threshold)
- **Maven Surefire** for unit tests
- **Maven Failsafe** for integration tests

### Documentation Updates

- Added CI/CD badges to README.md
- Added CI/CD section to Table of Contents
- Linked to detailed CI/CD documentation

## 🔐 Required Setup

### GitHub Secrets (Optional)

Add these in `Settings → Secrets and variables → Actions`:

| Secret | Purpose |
|--------|---------|
| `DOCKER_USERNAME` | Docker Hub username (optional) |
| `DOCKER_PASSWORD` | Docker Hub token (optional) |
| `SLACK_WEBHOOK_URL` | Slack notifications (optional) |

### GitHub Environments

Create in `Settings → Environments`:

1. **staging** - Automatic deployment from main branch
2. **production** - Manual approval required

## 📊 Pipeline Features

### Automated Testing
- ✅ Unit tests with JUnit 5
- ✅ Integration tests
- ✅ Code coverage reporting (JaCoCo)
- ✅ Test result artifacts

### Code Quality
- ✅ Maven Checkstyle (optional)
- ✅ SpotBugs analysis (optional)
- ✅ Code formatting validation

### Security
- ✅ OWASP dependency vulnerability scanning
- ✅ Docker image scanning with Trivy
- ✅ GitHub Security integration
- ✅ SBOM generation

### Docker
- ✅ Multi-stage builds
- ✅ Multi-platform support (amd64, arm64)
- ✅ GitHub Container Registry
- ✅ Layer caching for faster builds

### Deployment
- ✅ Automated staging deployment
- ✅ Manual production approval
- ✅ Smoke tests
- ✅ Health checks

## 🚀 How It Works

### On Pull Request
1. PR checks run automatically
2. Validates Maven POM
3. Runs quick tests
4. Checks PR title format
5. Comments with results

### On Push to Main
1. Full build and test suite
2. Code coverage analysis
3. Security scanning
4. Docker image building
5. Deploy to staging
6. Wait for production approval
7. Deploy to production

### Weekly Automated Tasks
- Dependency update checks
- Dependabot PRs for Maven, Actions, Docker

## 📈 Monitoring

### Status Badges
View in README.md:
- CI/CD Pipeline status
- Docker Build status

### Viewing Pipeline Runs
1. Go to repository Actions tab
2. Select workflow from sidebar
3. View logs and artifacts

## 🎯 Next Steps

### Immediate Actions
1. **Add GitHub Secrets** (if using external registries)
2. **Create Environments** (staging, production)
3. **Configure branch protection** rules for main branch
4. **Test the pipeline** by creating a PR

### Future Enhancements
- [ ] Add performance testing
- [ ] Implement canary deployments
- [ ] Add A/B testing capability
- [ ] Integrate with monitoring tools (Prometheus, Grafana)
- [ ] Add automated rollback
- [ ] Enhance security scanning

## ✅ Verification

The application is currently running successfully:
```bash
curl 'http://localhost:8080/api/v1/hotels/search?city=Tokyo&checkIn=2026-01-20&checkOut=2026-01-23&guests=2'
```

Response: ✅ Working (Budget Stay Tokyo returned)

## 📚 Documentation

- Main documentation: [docs/CI-CD.md](docs/CI-CD.md)
- README updated with CI/CD section
- Status badges added
- PR template created

---
