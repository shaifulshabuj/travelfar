## Description

<!-- Provide a brief description of the changes in this PR -->

## Type of Change

<!-- Mark the relevant option with an 'x' -->

- [ ] 🐛 Bug fix (non-breaking change which fixes an issue)
- [ ] ✨ New feature (non-breaking change which adds functionality)
- [ ] 💥 Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] 📝 Documentation update
- [ ] 🔧 Configuration change
- [ ] ♻️ Code refactoring
- [ ] ⚡ Performance improvement
- [ ] ✅ Test update

## Related Issues

<!-- Link to related issues using #issue_number -->

Closes #
Related to #

## Changes Made

<!-- List the main changes made in this PR -->

- 
- 
- 

## Testing

<!-- Describe the tests you ran to verify your changes -->

- [ ] Unit tests pass (`mvn test`)
- [ ] Integration tests pass (`mvn verify`)
- [ ] Manual testing completed
- [ ] Docker build succeeds (`docker-compose up`)

### Test Commands Run

```bash
# Add commands you ran for testing

```

## Checklist

<!-- Mark completed items with an 'x' -->

- [ ] My code follows the engineering standards
- [ ] I have performed a self-review of my code
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix is effective or that my feature works
- [ ] New and existing unit tests pass locally with my changes
- [ ] Any dependent changes have been merged and published
- [ ] I have checked my code and corrected any misspellings

## Screenshots (if applicable)

<!-- Add screenshots to help explain your changes -->

## Additional Notes

<!-- Add any additional notes for reviewers -->

## Deployment Notes

<!-- Any special deployment considerations? Database migrations? Configuration changes? -->

---

**Reviewer Guidelines:**
- Verify code follows layered architecture (Controller → Service → Repository)
- Check for proper error handling and logging
- Ensure no business logic in controllers
- Validate test coverage for new code
- Review for security implications
