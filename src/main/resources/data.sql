-- ============================================================
-- Fixly — Seed Data
-- Service Categories initialization
-- ============================================================

INSERT IGNORE INTO service_categories (id, name, description, icon_url, created_at)
VALUES
    (1, 'Electrical',        'Wiring issues, switchboard repairs, installations, lighting fixes and more.',     '⚡', NOW()),
    (2, 'Plumbing',          'Leak repairs, pipe fitting, drainage cleaning, bathroom fittings and more.',     '🔧', NOW()),
    (3, 'Painting',          'Interior & exterior painting, wall repairs, waterproofing and touch-ups.',       '🎨', NOW()),
    (4, 'Carpentry',         'Furniture repair, door installation, modular work and more.',                   '🪵', NOW()),
    (5, 'Appliance Repair',  'AC servicing, refrigerator repair, washing machine fixes and more.',            '🏠', NOW()),
    (6, 'Home Maintenance',  'General upkeep, cleaning, pest control, deep cleaning services.',               '🔨', NOW());
