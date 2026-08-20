document.addEventListener('DOMContentLoaded', () => {
  // Smooth interactive reveal for cards on scroll
  const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
  };

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.style.opacity = '1';
        entry.target.style.transform = 'translateY(0)';
        observer.unobserve(entry.target);
      }
    });
  }, observerOptions);

  document.querySelectorAll('.section-block').forEach(block => {
    block.style.opacity = '0';
    block.style.transform = 'translateY(15px)';
    block.style.transition = 'opacity 0.4s ease, transform 0.4s ease';
    observer.observe(block);
  });
});
