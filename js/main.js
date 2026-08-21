document.addEventListener('DOMContentLoaded', () => {
  // 1. Pinned Scroll Percentage & Progress Bar
  const scrollPct = document.getElementById('scroll-pct');
  const stBar = document.getElementById('st-bar');

  window.addEventListener('scroll', () => {
    const scrollTop = window.scrollY;
    const docHeight = document.documentElement.scrollHeight - window.innerHeight;
    const progress = docHeight > 0 ? Math.min(Math.max((scrollTop / docHeight) * 100, 0), 100) : 0;

    if (scrollPct) scrollPct.textContent = `(${Math.round(progress)}%)`;
    if (stBar) stBar.style.width = `${progress}%`;
  });

  // 2. Cursor Hover Preview Card (Only on Non-Touch Screens)
  const isTouchDevice = 'ontouchstart' in window || navigator.maxTouchPoints > 0;
  if (!isTouchDevice) {
    const previewCard = document.getElementById('itemPreviewCard');
    const previewImg = document.getElementById('previewImg');
    const previewTier = document.getElementById('previewTier');
    const previewRoster = document.getElementById('previewRoster');
    const archiveItems = document.querySelectorAll('.archive-item');

    let mouseX = 0;
    let mouseY = 0;
    let currentX = 0;
    let currentY = 0;

    window.addEventListener('mousemove', (e) => {
      mouseX = e.clientX;
      mouseY = e.clientY;
    });

    function renderCursor() {
      currentX += (mouseX - currentX) * 0.15;
      currentY += (mouseY - currentY) * 0.15;

      if (previewCard) {
        previewCard.style.left = `${currentX + 15}px`;
        previewCard.style.top = `${currentY + 15}px`;
      }
      requestAnimationFrame(renderCursor);
    }
    renderCursor();

    archiveItems.forEach((item) => {
      item.addEventListener('mouseenter', () => {
        const img = item.getAttribute('data-img');
        const tier = item.getAttribute('data-tier');
        const roster = item.getAttribute('data-roster');

        if (img && previewImg) previewImg.src = img;
        if (tier && previewTier) previewTier.textContent = tier;
        if (roster && previewRoster) previewRoster.textContent = roster;

        if (previewCard) {
          previewCard.style.opacity = '1';
          previewCard.style.transform = 'translate(-50%, -50%) scale(1)';
        }
      });

      item.addEventListener('mouseleave', () => {
        if (previewCard) {
          previewCard.style.opacity = '0';
          previewCard.style.transform = 'translate(-50%, -50%) scale(0.9)';
        }
      });
    });
  }
});
